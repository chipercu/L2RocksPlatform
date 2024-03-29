package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

import java.util.concurrent.atomic.AtomicReference;

@GraphQLTypeOutObject("depersonalization")
public class GQueryDepersonalization {

    public static AtomicReference<DepersonalizationStage> stage = new AtomicReference<>(DepersonalizationStage.IDLE);

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Статус процесса копирования обезличенной базы данных")
    public static GraphQLQuery<RemoteObject, GDepersonalizationStatus> getStatus() throws PlatformException {
        GraphQLQuery<RemoteObject, GDepersonalizationStatus> query = new GraphQLQuery<RemoteObject, GDepersonalizationStatus>() {
            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public GDepersonalizationStatus execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return new GDepersonalizationStatus(stage.get());
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.READ);
    }
}
