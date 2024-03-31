package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.license.LicenseBuilder;
import com.fuzzy.subsystem.core.domainobject.license.LicenseEditable;
import com.fuzzy.subsystem.core.domainobject.license.LicenseReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.remote.licensenotification.RCLicenseNotification;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import com.fuzzy.subsystems.utils.StringUtils;

import java.time.Instant;

public class RCLicenseImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicense {

    private static final int ACTUAL_VERSION = 3;

    private final RemovableResource<LicenseEditable> licenseRemovableResource;
    private final LicenseManager licenseManager;
    PrimaryKeyValidator primaryKeyValidator;
    private final RCExecutor<RCLicenseNotification> rCLicenseNotificationsExecutor;

    public RCLicenseImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        licenseRemovableResource = resources.getRemovableResource(LicenseEditable.class);
        licenseManager = new LicenseManager(component, resources);
        primaryKeyValidator = new PrimaryKeyValidator(false);
        rCLicenseNotificationsExecutor = new RCExecutor<>(resources, RCLicenseNotification.class);
    }

    @Override
    public LicenseReadable create(LicenseBuilder builder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        LicenseEditable licenseEditable = licenseRemovableResource.create(transaction);
        if (!builder.isContainLicenseKey() || StringUtils.isEmpty(builder.getLicenseKey())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(LicenseReadable.class, LicenseReadable.FIELD_LICENSE_KEY);
        }
        LicenseManager.License license = licenseManager.decryptLicense(builder.getLicenseKey());
//        if (license.getVersion() != ACTUAL_VERSION) { //TODO раскомментить 1.05.2023
//            throw CoreExceptionBuilder.buildInvalidLicenseVersionException();
//        }
        if (license.getExpirationTime().isBefore(Instant.now())) {
            throw CoreExceptionBuilder.buildLicenseIsExpiredException();
        }
        setFields(licenseEditable, builder, context);
        licenseRemovableResource.save(licenseEditable, transaction);
        licenseManager.actualizeCommonLicense(context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.License.TYPE_CREATE),
                new SyslogStructDataTarget(CoreTarget.TYPE_LICENSE, licenseEditable.getId())
                        .withParam(CoreParameter.License.LICENSE_KEY, license.getLicenseKey().substring(0, 9).concat("**********")),
                context
        );
        return licenseEditable;
    }

    @Override
    public Long remove(long licenseId, ContextTransaction context) throws PlatformException {
        LicenseEditable licenseEditable = primaryKeyValidator.validateAndGet(licenseId, licenseRemovableResource, context.getTransaction());
        LicenseManager.License license = licenseManager.decryptRcCryptedLicense(licenseEditable.getLicenseKey(), context);

        rCLicenseNotificationsExecutor.exec(rcLicenseNotification -> rcLicenseNotification.onBeforeRemoveLicense(licenseId, context));

        licenseRemovableResource.remove(licenseEditable, context.getTransaction());
        licenseManager.actualizeCommonLicense(context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.License.TYPE_REMOVE),
                new SyslogStructDataTarget(CoreTarget.TYPE_LICENSE, licenseId)
                        .withParam(CoreParameter.License.LICENSE_KEY, license.getLicenseKey().substring(0, 9).concat("**********")),
                context
        );
        return licenseId;
    }

    private void setFields(LicenseEditable licenseEditable, LicenseBuilder builder, ContextTransaction context) throws PlatformException {
        if (builder.isContainLicenseKey()) {
            licenseManager.validateUnique(builder.getLicenseKey(), context);
            byte[] encodedLicenseKey = licenseManager.encryptLicenseKey(builder.getLicenseKey(), context);
            licenseEditable.setLicenseKey(encodedLicenseKey);
        }
    }
}
