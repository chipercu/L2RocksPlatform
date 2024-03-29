package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.enums.ListSourceType;
import com.fuzzy.subsystem.core.graphql.query.field.GAdditionalField;
import com.fuzzy.subsystem.core.remote.additionalfield.AdditionalFieldCreatingBuilder;
import com.fuzzy.subsystem.core.remote.additionalfield.AdditionalFieldUpdatingBuilder;
import com.fuzzy.subsystem.core.remote.additionalfield.RCAdditionalField;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

@GraphQLTypeOutObject("mutation_additional_field")
public class GMutationAdditionalField {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATA_TYPE = "data_type";
    private static final String LIST_SOURCE_TYPE = "list_source_type";
    private static final String ORDER = "order";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Создание дополнительного поля")
    public static GraphQLQuery<RemoteObject, GAdditionalField> create(
            @GraphQLDescription("Название")
            @NonNull @GraphQLName(NAME) final String name,
            @GraphQLDescription("Тип данных")
            @NonNull @GraphQLName(DATA_TYPE) final FieldDataType dataType,
            @GraphQLDescription("Тип источника списка")
            @GraphQLName(LIST_SOURCE_TYPE) final GOptional<ListSourceType> listSourceType
    ) {
        GraphQLQuery<RemoteObject, GAdditionalField> query = new GraphQLQuery<>() {

            private RCAdditionalField rcAdditionalField;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAdditionalField = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalField.class);
            }

            @Override
            public GAdditionalField execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                AdditionalFieldCreatingBuilder builder = new AdditionalFieldCreatingBuilder(
                        EmployeeReadable.class.getName(), name, dataType, listSourceType.get());
                AdditionalFieldReadable additionalField = rcAdditionalField.create(builder, context);
                return new GAdditionalField(additionalField);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.CREATE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Редактирование дополнительного поля")
    public static GraphQLQuery<RemoteObject, GAdditionalField> update(
            @GraphQLDescription("Идентификатор обновляемого дополнительного поля")
            @NonNull @GraphQLName(ID) final long additionalFieldId,
            @GraphQLDescription("Название")
            @GraphQLName(NAME) final GOptional<String> name,
            @GraphQLDescription("Тип данных")
            @GraphQLName(DATA_TYPE) final GOptional<FieldDataType> dataType
    ) {
        GraphQLQuery<RemoteObject, GAdditionalField> query = new GraphQLQuery<RemoteObject, GAdditionalField>() {

            private RCAdditionalField rcAdditionalField;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAdditionalField = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalField.class);
            }

            @Override
            public GAdditionalField execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                AdditionalFieldUpdatingBuilder builder = new AdditionalFieldUpdatingBuilder();
                if (name.isPresent()) {
                    builder.setName(name.get());
                }
                if (dataType.isPresent()) {
                    builder.setDataType(dataType.get());
                }
                AdditionalFieldReadable additionalField = rcAdditionalField.update(additionalFieldId, builder, context);
                return new GAdditionalField(additionalField);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Порядок расположения")
    public static GraphQLQuery<RemoteObject, Boolean> order(
            @NonNull @GraphQLDescription("Тип данных")
            @GraphQLName(ORDER) final ArrayList<Long> order
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<>() {

            private RCAdditionalField rcAdditionalField;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAdditionalField = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalField.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                rcAdditionalField.order(order, context);
                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Удаление дополнительного поля")
    public static GraphQLQuery<RemoteObject, Boolean> remove(
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ID) final long additionalFieldId
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private RCAdditionalField rcAdditionalField;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAdditionalField = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalField.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcAdditionalField.remove(additionalFieldId, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.DELETE);
    }
}