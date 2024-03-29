package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.table.*;
import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleEditable;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.logoninfo.LogonInfoReadable;
import com.fuzzy.subsystem.core.remote.authentication.AuthenticationCreatingBuilder;
import com.fuzzy.subsystem.core.remote.authentication.RCAuthentication;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.i18n.MessageResourceSource;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.15.x",
        previousVersion = "1.0.14.x"
)
public class CoreUpdate1_0_15 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_15(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        updateAdminAccessRoleName();
        createAuthenticationTable(schema);
        createEmployeeAuthenticationTable(schema);
        initAuthenticationInfo();
    }

    private void updateAdminAccessRoleName() {
        executeQuery(new Query<Void>() {

            private EditableResource<AccessRoleEditable> accessRoleEditableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleEditableResource = resources.getEditableResource(AccessRoleEditable.class);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                accessRoleEditableResource.forEach(accessRole -> {
                    if (accessRole.isAdmin()) {
                        switch (accessRole.getName()) {
                            case "Application administrator":
                                accessRole.setName("Administrator");
                                break;
                            case "Прикладной администратор":
                                accessRole.setName("Администратор");
                                break;
                            default:
                                return;
                        }
                        accessRoleEditableResource.save(accessRole, transaction);
                    }
                }, transaction);
                return null;
            }
        });
    }

    private void createAuthenticationTable(Schema schema) {
        TField nameField = new TField("name", String.class);
        TField typeField = new TField("type", String.class);
        List<TField> fields = List.of(
                nameField,
                typeField
        );
        List<THashIndex> hashIndexes = List.of(
                new THashIndex(nameField),
                new THashIndex(typeField)
        );
        List<TPrefixIndex> prefixIndexes = List.of(
                new TPrefixIndex(nameField)
        );
        schema.createTable(new Table("Authentication", CoreSubsystemConsts.UUID, fields,
                hashIndexes, prefixIndexes, Collections.emptyList(), Collections.emptyList()));
    }

    private void createEmployeeAuthenticationTable(Schema schema) {
        TField employeeIdField = new TField("employee_id",
                new TableReference("Employee", CoreSubsystemConsts.UUID));
        TField authenticationIdField = new TField("authentication_id",
                new TableReference("Authentication", CoreSubsystemConsts.UUID));
        List<TField> fields = List.of(
                employeeIdField,
                authenticationIdField
        );
        List<THashIndex> hashIndexes = List.of(
                new THashIndex(employeeIdField),
                new THashIndex(authenticationIdField),
                new THashIndex(employeeIdField, authenticationIdField)
        );
        schema.createTable(new Table("EmployeeAuthentication", CoreSubsystemConsts.UUID, fields, hashIndexes));
    }

    private void initAuthenticationInfo() {
        executeQuery(new Query<Void>() {

            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private ReadableResource<LogonInfoReadable> logonInfoReadableResource;
            private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
            private RCAuthentication rcAuthentication;
            private RCEmployeeAuthentication rcEmployeeAuthentication;
            private LanguageGetter languageGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                logonInfoReadableResource = resources.getReadableResource(LogonInfoReadable.class);
                employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
                rcAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCAuthentication.class);
                rcEmployeeAuthentication =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
                languageGetter = new LanguageGetter(resources);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                CoreSubsystem component = Subsystems.getInstance().getCluster().getAnyLocalComponent(CoreSubsystem.class);
                Language language = languageGetter.getSystem(transaction);
                ResourceBundle resourceBundle = MessageResourceSource.get(CoreSubsystemConsts.UUID, language.getLocale());
                String authenticationName =
                        resourceBundle.getString(CoreSubsystemConsts.Localization.AuthenticationName.INTEGRATED);
                AuthenticationCreatingBuilder builder = new AuthenticationCreatingBuilder(
                        authenticationName, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED);
                ContextTransaction<?> context = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
                AuthenticationReadable authentication = rcAuthentication.create(builder, context);
                LogonType logonType = component.getConfig().getLogonType();
                employeeReadableResource.forEach(employee -> {
                    String authId = logonType == LogonType.LOGIN ? employee.getLogin() : employee.getEmail();
                    if (StringUtils.isEmpty(authId) || employee.getPasswordHash() == null
                            || employee.getPasswordHash().length == 0) {
                        return;
                    }
                    HashFilter filter = new HashFilter(LogonInfoReadable.FIELD_EMPLOYEE_ID, employee.getId());
                    LogonInfoReadable logonInfo = logonInfoReadableResource.find(filter, transaction);
                    if (logonInfo == null || !logonInfo.getEnabledLogon()) {
                        return;
                    }
                    filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employee.getId());
                    if (employeeAccessRoleReadableResource.find(filter, transaction) == null) {
                        return;
                    }
                    rcEmployeeAuthentication.assignAuthenticationToEmployee(authentication.getId(), employee.getId(), context);
                }, transaction);
                return null;
            }
        });
    }
}