package com.fuzzy.subsystem.core;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.impl.ContextImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.platform.service.detectresource.DetectLowResourceService;
import com.fuzzy.platform.service.detectresource.observer.ResourceObservable;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.core.remote.crypto.RCCryptoImpl;
import com.fuzzy.subsystem.core.remote.systemevent.ResourceObserverImpl;
import com.fuzzy.subsystem.core.scheduler.systemevents.MailSendSystemEventSchedulerQuery;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.core.service.licenseservice.LicenseJobService;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.subsystem.Info;
import com.fuzzy.subsystems.subsystem.SdkInfoBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;
import com.fuzzy.subsystems.utils.CompositeSystemQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@com.infomaximum.cluster.anotation.Info(uuid = CoreSubsystemConsts.UUID)
public class CoreSubsystem extends Subsystem {

    private final static Logger log = LoggerFactory.getLogger(CoreSubsystem.class);
    public static final Info INFO = new SdkInfoBuilder(CoreSubsystemConsts.UUID, CoreSubsystem.class)
            .withDependence(DatabaseComponent.class)
            .build();

    private final CoreConfig config;
    private ResourceObservable resourceObservable;
    private DetectLowResourceService detectLowResourceService;
    private final SystemEventService systemEventService;
    private LicenseManager.CommonLicense commonLicense;
    private final LicenseJobService licenseJobService;


    public CoreSubsystem() throws PlatformException {
        this.config = new CoreConfig.Builder(INFO, Subsystems.getInstance().getConfig()).build();
        RCCryptoImpl.initCrypto(this.config.getSecretKeyPath());
        systemEventService = new SystemEventService();
        licenseJobService = new LicenseJobService();
    }

    @Override
    public Info getInfo() {
        return INFO;
    }

    @Override
    public CoreConfig getConfig() {
        return config;
    }

    public SystemEventService getSystemEventService() {
        return systemEventService;
    }

    public ResourceObservable getLowResourceObservable() {
        return resourceObservable;
    }

    public LicenseManager.CommonLicense getCommonLicense() {
        return commonLicense;
    }

    public void setCommonLicense(LicenseManager.CommonLicense commonLicense) {
        this.commonLicense = commonLicense;
    }

    public LicenseJobService getLicenseJobService() {
        return licenseJobService;
    }

    @Override
    public QuerySystem<Void> onStart() {
        resourceObservable = new ResourceObservable(
                new ResourceObserverImpl(this)
        );

        detectLowResourceService = new DetectLowResourceService(resourceObservable, (t, e) -> {
            try {
                log.error("Application crashing ", e);
                SecurityLog.info(
                        new SyslogStructDataEvent(CoreEvent.System.TYPE_CRUSH),
                        new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                        new ContextImpl(new SourceSystemImpl())
                );
            } catch (Throwable thr) {
                log.error("Exception", thr);
            } finally {
                System.exit(1);
            }
        });
        detectLowResourceService.start(Duration.ofSeconds(10));
        return new CompositeSystemQuery(List.of(
                new CryptoInitQuery(),
                new AdminAccessRoleInitQuery(),
                new MailSendSystemEventSchedulerQuery(this),
                new SetCommonLicenseQuery(this)
        ));
    }

    @Override
    public void onDestroy() {
        if (Objects.nonNull(detectLowResourceService)) {
            detectLowResourceService.onDestroy();
        }
    }

    private static class CryptoInitQuery extends QuerySystem<Void> {

        @Override
        public void prepare(ResourceProvider resources) {

        }

        @Override
        public Void execute(ContextTransaction context) throws PlatformException {
            RCCryptoImpl.createSecretKeyIfNotExists();
            return null;
        }
    }

    private static class AdminAccessRoleInitQuery extends QuerySystem<Void> {

        private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
        private RControllerAccessRolePrivileges rControllerAccessRolePrivileges;

        @Override
        public void prepare(ResourceProvider resources) {
            accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
            rControllerAccessRolePrivileges = resources.getQueryRemoteController(
                    CoreSubsystem.class, RControllerAccessRolePrivileges.class);
        }

        @Override
        public Void execute(ContextTransaction context) throws PlatformException {
            PrivilegeValue[] privilegeValues = CorePrivilege.getAdminPrivileges();
            HashFilter filter = new HashFilter(AccessRoleReadable.FIELD_ADMIN, true);
            accessRoleReadableResource.forEach(filter, accessRole ->
                    rControllerAccessRolePrivileges.setPrivilegesToAccessRole(
                            accessRole.getId(), privilegeValues, context), context.getTransaction());
            return null;
        }
    }

    private static class SetCommonLicenseQuery extends QuerySystem<Void> {
        private final CoreSubsystem component;
        private LicenseManager licenseManager;

        public SetCommonLicenseQuery(CoreSubsystem component) {
            this.component = component;
        }

        @Override
        public void prepare(ResourceProvider resources) throws PlatformException {
            licenseManager = new LicenseManager(component, resources);
        }

        @Override
        public Void execute(ContextTransaction context) throws PlatformException {
            licenseManager.actualizeCommonLicense(context);
            return null;
        }
    }
}