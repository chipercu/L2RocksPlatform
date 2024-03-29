package com.fuzzy.subsystem.core.license;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.license.LicenseReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import com.fuzzy.subsystem.core.license.enums.ResetPeriod;
import com.fuzzy.subsystem.core.license.updater.LicenseSchemeChainUpdater;
import com.fuzzy.subsystem.core.license.updater.LicenseSchemeChainUpdaterImpl;
import com.fuzzy.subsystem.core.remote.crypto.RCCrypto;
import com.fuzzy.subsystem.core.scheduler.license.ResetLicenseParameterCurrentStateJob;
import com.fuzzy.subsystem.core.scheduler.license.SetCommonLicenseJob;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.scheduler.SimpleCronTrigger;
import com.fuzzy.subsystems.scheduler.SimpleRepeatableTrigger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.BiFunction;
import java.util.zip.GZIPInputStream;

public class LicenseManager {

    private static final long UNLIMITED = -1L;
    private static final int ACTUAL_VERSION = 5;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final CoreSubsystem component;
    private final ReadableResource<LicenseReadable> licenseReadableResource;
    private final RCCrypto rcCrypto;
    private static final PublicKey publicKey;

    private static final Logger logger = LoggerFactory.getLogger(LicenseManager.class);

    static {
        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp+2gkV6J05wEO7aRw5/fbc3OnznY86dx7WJyDL1Cx81ci5VYvAq0JpSsb8lUbmkrwVFJeV4Tak9vAe98GBVOian4Fzn20gHyuWR5tIzxRJzV5aMhNAYo6IUOyjrHEsgdX/iyFbvmI6Qw30zgEmeTFfdjzsPwPuGZtyGFZ3pXpnwTrH+hhLNIJp94tRC2cqw9Cw37gmfmDFI0IIjiAHtYH0fet8P4QfJgOxZRQ7krbjh+hf0s5cjNjsFu/fPEJz4JZqGVxU1eLuSqjfEPZoc+FIyf2FtvfrJg+H+YW/t7RHWEsk2jzpUkf+eEdx3sAGy9Ih6Otf+iAz3mEzuOCs8PowIDAQAB";

        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LicenseManager(CoreSubsystem component, ResourceProvider resources) {
        licenseReadableResource = resources.getReadableResource(LicenseReadable.class);
        rcCrypto = resources.getQueryRemoteController(CoreSubsystem.class, RCCrypto.class);
        this.component = component;
    }

    public void validateUnique(String base64LicenseKey, ContextTransaction context) throws PlatformException {
        License targetLicense = decryptLicense(base64LicenseKey);
        HashSet<License> licenses = collectLicenses(context);
        for (License license : licenses) {
            if (license.getUuid().equals(targetLicense.getUuid())) {
                throw GeneralExceptionBuilder.buildNotUniqueValueException("license_key", targetLicense.getLicenseKey().substring(0, 8) + "********");
            }
        }
        boolean hasResetParameters = hasResetParameters(targetLicense);
        if (hasResetParameters) {
            HashSet<License> actualLicenses = collectActualLicenses(context);
            for (License license : actualLicenses) {
                if (hasResetParameters(license)) {
                    throw CoreExceptionBuilder.buildLicenseResetParametersException();
                }
            }
        }
    }

    private boolean hasResetParameters(License license) {
        HashMap<String, HashMap<LicenseParameter, ParameterLimit>> targetLicenseModulesParametersLimits = license.getModulesParametersLimits();
        boolean hasResetParameters = false;
        for (Map.Entry<String, HashMap<LicenseParameter, ParameterLimit>> e : targetLicenseModulesParametersLimits.entrySet()) {
            HashMap<LicenseParameter, ParameterLimit> licenseParameterParameterLimitHashMap = e.getValue();
            for (Map.Entry<LicenseParameter, ParameterLimit> entry : licenseParameterParameterLimitHashMap.entrySet()) {
                ParameterLimit parameterLimit = entry.getValue();
                if (parameterLimit.firstResetDate != null || parameterLimit.resetPeriod != null) {
                    hasResetParameters = true;
                    break;
                }
            }
            if (hasResetParameters) {
                break;
            }
        }
        return hasResetParameters;
    }

    public byte[] encryptLicenseKey(String licenseKey, ContextTransaction context) throws PlatformException {
        return rcCrypto.encrypt(licenseKey, context);
    }

    public License decryptRcCryptedLicense(byte[] rcCryptedLicenseKey, ContextTransaction context) throws PlatformException {
        String signedBase64LicenseKey = rcCrypto.decryptAsString(rcCryptedLicenseKey, context);
        if (signedBase64LicenseKey == null) {
            logger.error("Unable to get signed license key because of RCCrypto error");
            return null;
        }
        return decryptLicense(signedBase64LicenseKey);
    }

    public License decryptLicense(String encryptedBase64LicenseKey) throws PlatformException {
        License license;
        try {
            byte[] commonLicenseKeyBytes = Base64.getDecoder().decode(encryptedBase64LicenseKey);
            byte[] bodySizeBytes = new byte[4];
            System.arraycopy(commonLicenseKeyBytes, 0, bodySizeBytes, 0, bodySizeBytes.length);
            int bodySize = ByteBuffer.wrap(bodySizeBytes).getInt();
            byte[] compressedBody = new byte[bodySize];
            System.arraycopy(commonLicenseKeyBytes, bodySizeBytes.length, compressedBody, 0, bodySize);
            int signatureSize = commonLicenseKeyBytes.length - compressedBody.length - bodySizeBytes.length;
            byte[] digitalSignature = new byte[signatureSize];
            System.arraycopy(commonLicenseKeyBytes, bodySizeBytes.length + compressedBody.length, digitalSignature, 0, digitalSignature.length);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(compressedBody);
            if (!signature.verify(digitalSignature)) {
                logger.error("Signature verification failed");
                throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
            }

            JSONObject licenseJson = parseToJson(compressedBody);
            try {
                LicenseSchemeChainUpdater schemeChainUpdater = new LicenseSchemeChainUpdaterImpl(ACTUAL_VERSION);
                schemeChainUpdater.updateToActual(licenseJson);

                String uuid = licenseJson.getAsString("uuid");
                int version = licenseJson.getAsNumber("version").intValue();
                String companyName = licenseJson.getAsString("company_name");
                Instant expirationTime = Instant.ofEpochMilli(licenseJson.getAsNumber("end_date").longValue());
                license = new License(uuid, version, companyName, expirationTime, encryptedBase64LicenseKey);
                setLimitsForLicense(license, licenseJson);
            } catch (Throwable e) {
                throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
            }
        } catch (PlatformException e) {
            throw e;
        } catch (Throwable e) {
            throw CoreExceptionBuilder.buildInvalidLicenseException(e);
        }
        return license;
    }

    private static JSONObject parseToJson(byte[] compressedBody) throws IOException, ParseException {
        String licenseBody;
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedBody));
             BufferedReader bf = new BufferedReader(new InputStreamReader(gis, CHARSET))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                result.append(line);
            }
            licenseBody = result.toString();
        }

        return (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(licenseBody);
    }

    private void setLimitsForLicense(License license, JSONObject licenseJson) throws PlatformException {
        JSONObject modulesLimitsJson = (JSONObject) licenseJson.get("modules_limits");
        for (Map.Entry<String, Object> entry : modulesLimitsJson.entrySet()) {
            String moduleName = entry.getKey();
            Object moduleLimitsObj = entry.getValue();
            HashMap<LicenseParameter, ParameterLimit> limitsMap = new HashMap<>();
            JSONObject moduleLimitsJson = (JSONObject) moduleLimitsObj;
            for (Map.Entry<String, Object> e : moduleLimitsJson.entrySet()) {
                String pKey = e.getKey();
                JSONObject parameterLimitJson = (JSONObject) e.getValue();
                LicenseParameter licenseParameter = LicenseParameter.ofKey(pKey);
                long limit = parameterLimitJson.getAsNumber("limit").longValue();
                Number firstResetDateNumber = parameterLimitJson.getAsNumber("first_reset_date");
                String resetPeriodString = parameterLimitJson.getAsString("reset_period");
                if (licenseParameter == null) {
                    throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
                }
                if (resetPeriodString != null) {
                    ResetPeriod resetPeriod = ResetPeriod.valueOf(resetPeriodString.toUpperCase());
                    Instant firstResetDate = Instant.ofEpochMilli(firstResetDateNumber.longValue());
                    limitsMap.put(licenseParameter, new ParameterLimit(limit, firstResetDate, resetPeriod));
                } else {
                    limitsMap.put(licenseParameter, new ParameterLimit(limit));
                }
            }
            license.getModulesParametersLimits().put(moduleName, limitsMap);
        }

        JSONObject businessRolesJson = (JSONObject) licenseJson.get("business_roles");
        for (Map.Entry<String, Object> entry : businessRolesJson.entrySet()) {
            String pKey = entry.getKey();
            Object pValue = entry.getValue();
            Number limit = (Number) pValue;
            BusinessRoleLimit businessRoleLimit = BusinessRoleLimit.ofKey(pKey);
            if (businessRoleLimit == null) {
                throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
            }
            license.getBusinessRolesLimits().put(businessRoleLimit, limit.longValue());
        }
    }

    private HashSet<License> collectLicenses(ContextTransaction context) throws PlatformException {
        HashSet<License> licenses = new HashSet<>();
        licenseReadableResource.forEach(licenseReadable -> {
            License license = decryptRcCryptedLicense(licenseReadable.getLicenseKey(), context);
            if (license != null) {
                licenses.add(license);
            }
        }, context.getTransaction());
        return licenses;
    }

    private HashSet<License> collectActualLicenses(ContextTransaction context) throws PlatformException {
        HashSet<License> licenses = new HashSet<>();
        licenseReadableResource.forEach(licenseReadable -> {
            try {
                License license = decryptRcCryptedLicense(licenseReadable.getLicenseKey(), context);
                if (license != null && !license.getExpirationTime().isBefore(Instant.now())) {
                    licenses.add(license);
                }
            } catch (PlatformException e) {
                logger.warn("Invalid license. LicenseReadableId={}", licenseReadable.getId(), e);
            }
        }, context.getTransaction());

        return licenses;
    }

    public CommonLicense getCommonLicense(ContextTransaction context) throws PlatformException {
        HashSet<License> licenses = collectActualLicenses(context);
        if (licenses.isEmpty()) {
            return null;
        }
        Instant time = null;
        for (License license : licenses) {
            if (time == null) {
                time = license.getExpirationTime();
                continue;
            }
            if (license.getExpirationTime().isBefore(time)) {
                time = license.getExpirationTime();
            }
        }

        CommonLicense commonLicense = new CommonLicense(time);

        for (License license : licenses) {
            HashMap<String, HashMap<LicenseParameter, ParameterLimit>> modulesParametersLimits = license.getModulesParametersLimits();
            for (Map.Entry<String, HashMap<LicenseParameter, ParameterLimit>> entry : modulesParametersLimits.entrySet()) {
                String moduleUUID = entry.getKey();
                HashMap<LicenseParameter, ParameterLimit> parameterLimitsHashMap = entry.getValue();
                if (!commonLicense.getModulesParametersLimits().containsKey(moduleUUID)) {
                    commonLicense.getModulesParametersLimits().put(moduleUUID, parameterLimitsHashMap);
                } else {
                    for (Map.Entry<LicenseParameter, ParameterLimit> e : parameterLimitsHashMap.entrySet()) {
                        LicenseParameter licenseParameter = e.getKey();
                        ParameterLimit parameterLimit = e.getValue();
                        if (licenseParameter.equals(LicenseParameter.MIN_SCRIPT_RUN_PERIOD)) {
                            commonLicense.getModulesParametersLimits().get(moduleUUID).merge(licenseParameter, parameterLimit,
                                    (oldValue, newValue) -> {
                                        if (oldValue.limit == -1L || newValue.limit == -1L) {
                                            parameterLimit.limit = -1L;
                                        } else {
                                            parameterLimit.limit = Math.min(oldValue.limit, newValue.limit);
                                        }
                                        return parameterLimit;
                                    });
                        } else {
                            commonLicense.getModulesParametersLimits().get(moduleUUID).merge(licenseParameter, parameterLimit,
                                    (parameterLimit1, parameterLimit2) -> {
                                        if (parameterLimit1.limit == UNLIMITED || parameterLimit2.limit == UNLIMITED) {
                                            parameterLimit.limit = UNLIMITED;
                                        } else {
                                            parameterLimit.limit = parameterLimit1.limit + parameterLimit2.limit;
                                        }
                                        //(Лицензия v3) В данный момент параметры, требующие обнуления текущего состояния присутствуют только в случае единственной актуальной лицензии.
                                        //В системе не может быть нескольких лицензий с параметрами обнуления.
                                        if ((parameterLimit1.firstResetDate != null && parameterLimit2.firstResetDate != null) ||
                                                parameterLimit1.resetPeriod != null && parameterLimit2.resetPeriod != null) {
                                            logger.error("Multiple licenses with reset parameters was detected");
                                            throw new RuntimeException();
                                        }
                                        //При изменении этой логики, также следует поменять  подход к суммированию параметров даты сброса и периода сброса.
                                        if (parameterLimit1.firstResetDate != null) {
                                            parameterLimit.firstResetDate = parameterLimit1.firstResetDate;
                                        } else if (parameterLimit2.firstResetDate != null) {
                                            parameterLimit.firstResetDate = parameterLimit2.firstResetDate;
                                        }
                                        if (parameterLimit1.resetPeriod != null) {
                                            parameterLimit.resetPeriod = parameterLimit1.resetPeriod;
                                        } else if (parameterLimit2.resetPeriod != null) {
                                            parameterLimit.resetPeriod = parameterLimit2.resetPeriod;
                                        }
                                        return parameterLimit;
                                    });
                        }
                    }
                }
            }
            HashMap<BusinessRoleLimit, Long> businessRolesLimits = license.getBusinessRolesLimits();
            for (Map.Entry<BusinessRoleLimit, Long> entry : businessRolesLimits.entrySet()) {
                BusinessRoleLimit businessRoleLimit = entry.getKey();
                Long limit = entry.getValue();
                commonLicense.getBusinessRolesLimits().merge(businessRoleLimit, limit, mergeBusinessRolesFunction);
            }
        }
        return commonLicense;
    }

    public void actualizeCommonLicense(ContextTransaction context) throws PlatformException {
        String jobName = component.getLicenseJobService().getCommonLicenseJobId().getAndSet(null);
        if (jobName != null) {
            component.getScheduler().removeJob(jobName);
        }
        removeOldResetParameterJobs();
        component.setCommonLicense(getCommonLicense(context));
        setCommonLicenseJob();
        addResetParameterJobs();
    }

    private void removeOldResetParameterJobs() throws PlatformException {
        ArrayList<String> allResetParameterJobs = component.getLicenseJobService().getAllResetParameterJobs();
        for (String jobId : allResetParameterJobs) {
            component.getScheduler().removeJob(jobId);
        }
        component.getLicenseJobService().clearResetParameterJobs();
    }

    private void addResetParameterJobs() throws PlatformException {
        if (component.getCommonLicense() == null) {
            return;
        }
        for (Map.Entry<String, HashMap<LicenseParameter, ParameterLimit>> entry : component.getCommonLicense().getModulesParametersLimits().entrySet()) {
            HashMap<LicenseParameter, ParameterLimit> licenseParameterHashMap = entry.getValue();
            for (Map.Entry<LicenseParameter, ParameterLimit> e : licenseParameterHashMap.entrySet()) {
                LicenseParameter licenseParameter = e.getKey();
                ParameterLimit parameterLimit = e.getValue();
                if (parameterLimit.resetPeriod == null) {
                    continue;
                }
                LocalDateTime firstResetDateTime = LocalDateTime.ofInstant(parameterLimit.firstResetDate, ZoneId.systemDefault());
                switch (parameterLimit.resetPeriod) {
                    case DAY -> {
                        String cron = String.format("%d %d %d * * ?",
                                firstResetDateTime.getSecond(),
                                firstResetDateTime.getMinute(),
                                firstResetDateTime.getHour()
                        );
                        String jobId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cron), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                        component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobId);
                    }
                    case WEEK -> {
                        String cron = String.format("%d %d %d ? * %s",
                                firstResetDateTime.getSecond(),
                                firstResetDateTime.getMinute(),
                                firstResetDateTime.getHour(),
                                firstResetDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase()
                        );
                        String jobId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cron), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                        component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobId);
                    }
                    case MONTH -> {
                        int dayOfMonth = firstResetDateTime.getDayOfMonth();
                        switch (dayOfMonth) {
                            case 31 -> {
                                String cron = String.format("%d %d %d L * ?",
                                        firstResetDateTime.getSecond(),
                                        firstResetDateTime.getMinute(),
                                        firstResetDateTime.getHour()
                                );
                                String jobId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cron), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                                component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobId);
                            }
                            case 29, 30 -> {
                                String cron = String.format("%d %d %d %d * ?",
                                        firstResetDateTime.getSecond(),
                                        firstResetDateTime.getMinute(),
                                        firstResetDateTime.getHour(),
                                        dayOfMonth
                                );
                                String jobId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cron), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                                component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobId);
                                String cronFeb = String.format("%d %d %d L 2 ?",
                                        firstResetDateTime.getSecond(),
                                        firstResetDateTime.getMinute(),
                                        firstResetDateTime.getHour()
                                );
                                String jobFebId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cronFeb), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                                component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobFebId);
                            }
                            default -> {
                                String cron = String.format("%d %d %d %d * ?",
                                        firstResetDateTime.getSecond(),
                                        firstResetDateTime.getMinute(),
                                        firstResetDateTime.getHour(),
                                        dayOfMonth
                                );
                                String jobId = component.getScheduler().scheduleJob(new SimpleCronTrigger(cron), () -> new ResetLicenseParameterCurrentStateJob(licenseParameter));
                                component.getLicenseJobService().addParameterResetJobId(licenseParameter, jobId);
                            }
                        }
                    }

                }
            }
        }
    }

    private void setCommonLicenseJob() throws PlatformException {
        CommonLicense commonLicense = component.getCommonLicense();
        if (commonLicense != null) {
            String jobId = component.getScheduler().scheduleJob(
                    new SimpleRepeatableTrigger(
                            commonLicense.getExpirationTime(),
                            Duration.ZERO,
                            0
                    ),
                    () -> new SetCommonLicenseJob(component)
            );
            component.getLicenseJobService().getCommonLicenseJobId().set(jobId);
        }
    }

    private final BiFunction<Long, Long, Long> mergeBusinessRolesFunction = (value1, value2) -> value1 == UNLIMITED || value2 == UNLIMITED ? UNLIMITED : value1 + value2;

    public static class CommonLicense {
        protected final Instant expirationTime;
        protected final HashMap<String, HashMap<LicenseParameter, ParameterLimit>> modulesParametersLimits;
        protected final HashMap<BusinessRoleLimit, Long> businessRolesLimits;

        private CommonLicense(Instant expirationTime) {
            this.expirationTime = expirationTime;
            modulesParametersLimits = new HashMap<>();
            businessRolesLimits = new HashMap<>();
        }

        public Instant getExpirationTime() {
            return expirationTime;
        }

        public HashMap<String, HashMap<LicenseParameter, ParameterLimit>> getModulesParametersLimits() {
            return modulesParametersLimits;
        }

        public HashMap<BusinessRoleLimit, Long> getBusinessRolesLimits() {
            return businessRolesLimits;
        }

        public long getParameterLimit(String moduleUUID, LicenseParameter licenseParameter) {
            if (modulesParametersLimits.containsKey(moduleUUID)) {
                HashMap<LicenseParameter, ParameterLimit> parameterLimits = modulesParametersLimits.get(moduleUUID);
                if (parameterLimits.containsKey(licenseParameter)) {
                    return parameterLimits.get(licenseParameter).limit;
                }
            }
            return 0;
        }

        public Instant getParameterFirstResetDate(String moduleUUID, LicenseParameter licenseParameter) {
            if (modulesParametersLimits.containsKey(moduleUUID)) {
                HashMap<LicenseParameter, ParameterLimit> parameterLimits = modulesParametersLimits.get(moduleUUID);
                if (parameterLimits.containsKey(licenseParameter)) {
                    return parameterLimits.get(licenseParameter).firstResetDate;
                }
            }
            return null;
        }

        public ResetPeriod getParameterResetPeriod(String moduleUUID, LicenseParameter licenseParameter) {
            if (modulesParametersLimits.containsKey(moduleUUID)) {
                HashMap<LicenseParameter, ParameterLimit> parameterLimits = modulesParametersLimits.get(moduleUUID);
                if (parameterLimits.containsKey(licenseParameter)) {
                    return parameterLimits.get(licenseParameter).resetPeriod;
                }
            }
            return null;
        }

        public long getBusinessRoleLimit(BusinessRoleLimit businessRoleLimit) {
            long limit = 0L;
            if (businessRolesLimits.containsKey(businessRoleLimit)) {
                limit = businessRolesLimits.get(businessRoleLimit);
            }
            return limit;
        }

        public Instant getNextResetDate(String moduleUUID, LicenseParameter licenseParameter) {
            Instant firstResetDate = getParameterFirstResetDate(moduleUUID, licenseParameter);
            ResetPeriod resetPeriod = getParameterResetPeriod(moduleUUID, licenseParameter);
            Instant endLicenseDate = getExpirationTime();
            if (firstResetDate == null || resetPeriod == null) {
                return null;
            }
            LocalDateTime endDateTime = LocalDateTime.ofInstant(endLicenseDate, ZoneId.systemDefault());
            LocalDateTime firstResetDateTime = LocalDateTime.ofInstant(firstResetDate, ZoneId.systemDefault());
            LocalDateTime resetLocalDate = LocalDateTime.ofInstant(firstResetDate, ZoneId.systemDefault());
            Instant now = Instant.now();
            while (resetLocalDate.isBefore(endDateTime)) {
                Instant nextResetDate = resetLocalDate.toInstant(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()));
                if (nextResetDate.isAfter(now) && nextResetDate.isBefore(endLicenseDate)) {
                    return nextResetDate;
                }
                final Period period = resetPeriod.toPeriod();
                switch (resetPeriod) {
                    case DAY, WEEK -> resetLocalDate = resetLocalDate.plus(period);
                    case MONTH -> {
                        int dayOfMonth = firstResetDateTime.getDayOfMonth();
                        switch (dayOfMonth) {
                            case 29, 30, 31 -> {
                                resetLocalDate = resetLocalDate.plus(period);
                                try {
                                    resetLocalDate = resetLocalDate.withDayOfMonth(dayOfMonth);
                                } catch (DateTimeException e) {
                                    int lengthOfMonth = resetLocalDate.toLocalDate().lengthOfMonth();
                                    resetLocalDate = resetLocalDate.withDayOfMonth(lengthOfMonth);
                                }
                            }
                            default -> resetLocalDate = resetLocalDate.plus(period);
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class License extends CommonLicense {
        private final int version;
        private final String companyName;
        private final String uuid;
        private final String licenseKey;

        private License(String uuid, int version, String companyName, Instant expirationTime, String licenseKey) {
            super(expirationTime);
            this.version = version;
            this.companyName = companyName;
            this.licenseKey = licenseKey;
            this.uuid = uuid;
        }

        public int getVersion() {
            return version;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getLicenseKey() {
            return licenseKey;
        }

        public String getUuid() {
            return uuid;
        }

    }

    public static class ParameterLimit {
        private long limit;
        private Instant firstResetDate;
        private ResetPeriod resetPeriod;

        private ParameterLimit(long limit, Instant firstResetDate, ResetPeriod resetPeriod) {
            this.limit = limit;
            this.firstResetDate = firstResetDate;
            this.resetPeriod = resetPeriod;
        }

        private ParameterLimit(long limit) {
            this(limit, null, null);
        }

        public long getLimit() {
            return limit;
        }

        public Instant getFirstResetDate() {
            return firstResetDate;
        }

        public ResetPeriod getResetPeriod() {
            return resetPeriod;
        }
    }

}
