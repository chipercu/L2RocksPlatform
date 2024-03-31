package com.fuzzy.subsystem.core.graphql.query.field;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.fieldsgetter.AdditionalFieldDescription;
import com.fuzzy.subsystem.core.remote.fieldsgetter.RCFieldsGetter;
import com.fuzzy.subsystem.core.remote.fieldsgetter.SystemFieldDescription;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQueryImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("field_query")
public class GQueryField {

    private static final String ID = "id";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Дополнительное поле по идентификатору")
    public static GraphQLQuery<RemoteObject, GAdditionalField> getAdditionalField(
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ID) final long additionalFieldId
    ) {
        GraphQLQuery<RemoteObject, GAdditionalField> query =
                new GPrimaryKeyQueryImpl<>(AdditionalFieldReadable.class, GAdditionalField::new, additionalFieldId);
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.CREATE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список системных полей")
    public static GraphQLQuery<RemoteObject, ArrayList<GSystemField>> getSystemFields() {
        GraphQLQuery<RemoteObject, ArrayList<GSystemField>> query = new GraphQLQuery<RemoteObject, ArrayList<GSystemField>>() {

            private RCFieldsGetter rcFieldsGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcFieldsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCFieldsGetter.class);
            }

            @Override
            public ArrayList<GSystemField> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<SystemFieldDescription> fields =
                        rcFieldsGetter.getSystemFields(EmployeeReadable.class.getName(), context);
                return fields.stream().map(GSystemField::new).collect(Collectors.toCollection(ArrayList::new));
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список дополнительных полей")
    public static GraphQLQuery<RemoteObject, ArrayList<GAdditionalField>> getAdditionalFields() {
        GraphQLQuery<RemoteObject, ArrayList<GAdditionalField>> query = new GraphQLQuery<RemoteObject, ArrayList<GAdditionalField>>() {

            private ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
            private RCFieldsGetter rcFieldsGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
                rcFieldsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCFieldsGetter.class);
            }

            @Override
            public ArrayList<GAdditionalField> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<AdditionalFieldDescription> fields =
                        rcFieldsGetter.getAdditionalFields(EmployeeReadable.class.getName(), context);
                ArrayList<GAdditionalField> gFields = new ArrayList<>(fields.size());
                for (AdditionalFieldDescription field : fields) {
                    AdditionalFieldReadable item =
                            additionalFieldReadableResource.get(field.getAdditionalFieldId(), context.getTransaction());
                    gFields.add(new GAdditionalField(item));
                }
                return gFields;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }
}
