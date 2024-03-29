package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
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
