package com.fuzzy.subsystem.core.remote.depersonalization;

import com.infomaximum.database.domainobject.DomainObjectSource;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.database.domainobject.iterator.IteratorEntity;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.utils.TypeConvert;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.rocksdb.RocksDBProvider;
import com.infomaximum.rocksdb.RocksDataBaseBuilder;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleEditable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyEditable;
import com.fuzzy.subsystem.core.domainobject.config.CoreConfigEditable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentEditable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeEditable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryEditable;
import com.fuzzy.subsystem.core.domainobject.employeeinvitationtoken.EmployeeInvitationTokenEditable;
import com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess.EmployeeTokenRestoreAccessEditable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordEditable;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

public class RControllerDepersonalizationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerDepersonalization {

    public RControllerDepersonalizationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
    }

    @Override
    public void depersonalize(Options options) throws PlatformException {
        try (RocksDBProvider rocksDBProvider = new RocksDataBaseBuilder().withPath(Paths.get(options.getDbPath())).build()) {
            DomainObjectSource domainObjectSource = new DomainObjectSource(rocksDBProvider, false);

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, AccessRoleEditable.class, accessRole -> {
                DepersonalizationUtils.depersonalizeField(accessRole::getName, accessRole::setName, accessRole.getId());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, ApiKeyEditable.class, apiKey -> {
                DepersonalizationUtils.depersonalizeField(apiKey::getName, apiKey::setName, apiKey.getId());
                DepersonalizationUtils.depersonalizeField(apiKey::getValue, apiKey::setValue, apiKey.getId());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, DepartmentEditable.class, department -> {
                DepersonalizationUtils.depersonalizeField(department::getName, department::setName, department.getId());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, EmployeeEditable.class, employee -> {
                employee.setEmail(employee.getId() + "@info.org");
                DepersonalizationUtils.depersonalizeField(employee::getFirstName, employee::setFirstName, null);
                DepersonalizationUtils.depersonalizeField(employee::getPatronymic, employee::setPatronymic, null);
                DepersonalizationUtils.depersonalizeField(employee::getSecondName, employee::setSecondName, null);
                employee.setPasswordHashWithSalt(UUID.randomUUID().toString());
                DepersonalizationUtils.depersonalizeField(employee::getPersonnelNumber, employee::setPersonnelNumber, null);
                DepersonalizationUtils.depersonalizeField(employee::getLogin, employee::setLogin, null);
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, EmployeeAuthorizationHistoryEditable.class, authHistory -> {
                authHistory.setLastPasswordChangeUtcTime(Instant.now());
                authHistory.setLastLogonUtcTime(Instant.now());
                DepersonalizationUtils.depersonalizeField(authHistory::getLastIpAddress, authHistory::setLastIpAddress, authHistory.getId());
                authHistory.setInvalidLogonCount((int) (100 * Math.random()));
                authHistory.setLastInvalidLogonUtcTime(Instant.now());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, EmployeeInvitationTokenEditable.class, invitationToken -> {
                DepersonalizationUtils.depersonalizeField(invitationToken::getToken, invitationToken::setToken, invitationToken.getId());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, EmployeeTokenRestoreAccessEditable.class, tokenRestoreAccess -> {
                DepersonalizationUtils.depersonalizeField(tokenRestoreAccess::getToken, tokenRestoreAccess::setToken, tokenRestoreAccess.getId());
            });

            DepersonalizationUtils.depersonalizeDomain(domainObjectSource, UsedPasswordEditable.class, usedPassword -> {
                usedPassword.setSaltyPasswordHash(UUID.randomUUID().toString().getBytes());
                usedPassword.setSalt(UUID.randomUUID().toString().getBytes());
            });
            depersonalizeSecurityConfigs(domainObjectSource);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    private void depersonalizeSecurityConfigs(DomainObjectSource domainObjectSource) throws DatabaseException {
        try (Transaction transaction = domainObjectSource.buildTransaction()) {

            HashFilter filter = new HashFilter(CoreConfigEditable.FIELD_NAME, CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH.getName());
            try (IteratorEntity<CoreConfigEditable> ie = transaction.find(CoreConfigEditable.class, filter)) {
                while (ie.hasNext()) {
                    CoreConfigEditable coreConfigEditable = ie.next();
                    coreConfigEditable.setValue(TypeConvert.pack((int)(Math.random() * 10 + 4)));
                    transaction.save(coreConfigEditable);
                }
            }

            filter = new HashFilter(CoreConfigEditable.FIELD_NAME, CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME.getName());
            try (IteratorEntity<CoreConfigEditable> ie = transaction.find(CoreConfigEditable.class, filter)) {
                while (ie.hasNext()) {
                    CoreConfigEditable coreConfigEditable = ie.next();
                    coreConfigEditable.setValue(TypeConvert.pack((long)(Math.random() * 6 + 4) * 10000000L));
                    transaction.save(coreConfigEditable);
                }
            }

            filter = new HashFilter(CoreConfigEditable.FIELD_NAME, CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT.getName());
            try (IteratorEntity<CoreConfigEditable> ie = transaction.find(CoreConfigEditable.class, filter)) {
                while (ie.hasNext()) {
                    CoreConfigEditable coreConfigEditable = ie.next();
                    coreConfigEditable.setValue(TypeConvert.pack((int)(Math.random() * 6 + 4)));
                    transaction.save(coreConfigEditable);
                }
            }
            transaction.commit();
        }
    }
}
