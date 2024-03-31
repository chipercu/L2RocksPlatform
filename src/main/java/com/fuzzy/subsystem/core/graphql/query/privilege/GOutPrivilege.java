package com.fuzzy.subsystem.core.graphql.query.privilege;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.privilege.PrivilegeGetter;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

import java.util.ArrayList;
import java.util.Arrays;

@GraphQLTypeOutObject("out_privilege")
public class GOutPrivilege implements RemoteObject {

    private String key;
    private ArrayList<AccessOperation> operations;

    public GOutPrivilege() {
    }

    public GOutPrivilege(String key, ArrayList<AccessOperation> operations) {
        this.key = key;
        this.operations = operations;
    }

    public GOutPrivilege(String key, AccessOperationCollection operations) {
        this.key = key;
        this.operations = new ArrayList<>(Arrays.asList(operations.getOperations()));
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Ключ")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @GraphQLField
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Отображаемое имя")
    public static GraphQLQuery<GOutPrivilege, String> getDisplayName() {
        GraphQLQuery<GOutPrivilege, String> query = new GraphQLQuery<GOutPrivilege, String>() {

            private LanguageGetter languageGetter;
            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                languageGetter = new LanguageGetter(resources);
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public String execute(GOutPrivilege source, ContextTransactionRequest context) throws PlatformException {
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                long authEmployeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
                Language language = languageGetter.getById(authEmployeeId, context.getTransaction());
                return privilegeGetter.getPrivilegeDisplayName(source.key, language, context);
            }
        };
        return new GAccessQuery<>(query, GAccessQuery.Operator.OR)
                .with(CorePrivilege.ACCESS_ROLE, AccessOperation.READ)
                .with(CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Возможные операции")
    public static GraphQLQuery<GOutPrivilege, ArrayList<AccessOperation>> getAvailableOperations() {
        GraphQLQuery<GOutPrivilege, ArrayList<AccessOperation>> query =
                new GraphQLQuery<GOutPrivilege, ArrayList<AccessOperation>>() {

            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public ArrayList<AccessOperation> execute(GOutPrivilege source, ContextTransactionRequest context)
                    throws PlatformException {
                AccessOperationCollection operationCollection = privilegeGetter.getAvailableOperations(source.key, context);
                return new ArrayList<>(Arrays.asList(operationCollection.getOperations()));
            }
        };
        return new GAccessQuery<>(query, GAccessQuery.Operator.OR)
                .with(CorePrivilege.ACCESS_ROLE, AccessOperation.READ)
                .with(CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Операции")
    public ArrayList<AccessOperation> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<AccessOperation> operations) {
        this.operations = operations;
    }
}
