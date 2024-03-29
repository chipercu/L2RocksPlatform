package com.fuzzy.subsystem.core.graphql.query.license;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.license.LicenseReadable;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.out.GOutDateTime;

import java.time.ZoneId;

@GraphQLTypeOutObject("license")
public class GLicense extends GDomainObject<LicenseReadable> {

    private static final String MONITORING_UUID = "com.infomaximum.subsystem.monitoring";
    private static final String AUTOMATION_UUID = "com.infomaximum.subsystem.automation";
    private static final String BI_DATA_UUID = "com.infomaximum.subsystem.bidata";

    private final boolean hidden;

    public GLicense(LicenseReadable source) {
        this(source, true);
    }

    public GLicense(LicenseReadable source, boolean hidden) {
        super(source);
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("ID лицензии")
    public long getId() {
        return getSource().getId();
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Версия")
    public static GraphQLQuery<GLicense, Integer> getVersion(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Integer execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getVersion();
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Имя")
    public static GraphQLQuery<GLicense, String> getName(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public String execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getCompanyName();
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Время окончания")
    public static GraphQLQuery<GLicense, GOutDateTime> getExpirationTime(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public GOutDateTime execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : GOutDateTime.of(license.getExpirationTime(), ZoneId.of("UTC"));
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Уникальный идентификатор")
    public static GraphQLQuery<GLicense, String> getUuid(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public String execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getUuid();
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Лицензионный ключ")
    public static GraphQLQuery<GLicense, String> getLicenseKey(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public String execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : (source.hidden ? license.getLicenseKey().substring(0, 8) + "********" : license.getLicenseKey());
            }
        };
    }


    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getDiskSpaceLimit(CoreSubsystem component) {

        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getParameterLimit(BI_DATA_UUID, LicenseParameter.DISK_SPACE);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getMinScriptRunPeriodLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getParameterLimit(AUTOMATION_UUID, LicenseParameter.MIN_SCRIPT_RUN_PERIOD);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getMailSendingLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getParameterLimit(AUTOMATION_UUID, LicenseParameter.MAIL_SENDING);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getMonitoringSimpleLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getParameterLimit(MONITORING_UUID, LicenseParameter.MONITORING_SIMPLE);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getMonitoringExtendedLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getParameterLimit(MONITORING_UUID, LicenseParameter.MONITORING_EXTENDED);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getBusinessUserLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getBusinessRoleLimit(BusinessRoleLimit.BUSINESS_USER);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getAnalystLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getBusinessRoleLimit(BusinessRoleLimit.ANALYST);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    public static GraphQLQuery<GLicense, Long> getAdminLimit(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private LicenseManager licenseManager;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseManager = new LicenseManager(component, resources);
            }

            @Override
            public Long execute(GLicense source, ContextTransactionRequest context) throws PlatformException {
                LicenseManager.License license = licenseManager.decryptRcCryptedLicense(source.getSource().getLicenseKey(), context);
                return license == null ? null : license.getBusinessRoleLimit(BusinessRoleLimit.ADMIN);
            }
        };
    }
}
