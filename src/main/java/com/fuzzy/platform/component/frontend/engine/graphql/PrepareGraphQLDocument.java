package com.fuzzy.platform.component.frontend.engine.graphql;

import com.fuzzy.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.fuzzy.cluster.graphql.executor.GraphQLExecutorPrepareImpl;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.ResourceProviderImpl;
import com.fuzzy.platform.sdk.graphql.fieldconfiguration.struct.FieldConfiguration;
import graphql.ExecutionInput;

import java.util.HashMap;

public class PrepareGraphQLDocument {

    private boolean queryPoolRequest;
    private HashMap<String, QueryPool.LockType> waitLockResources;
    private GraphQLExecutorPrepareImpl.PrepareDocumentRequest prepareDocumentRequest;

    public PrepareGraphQLDocument(GraphQLExecutorPrepareImpl graphQLExecutorPrepare, ExecutionInput executionInput) throws PlatformException {
        waitLockResources = new HashMap<>();
        boolean[] resultQueryPoolRequest = new boolean[1];
        try {
            prepareDocumentRequest = graphQLExecutorPrepare.prepare(
                    executionInput,
                    new GraphQLExecutorPrepareImpl.PrepareFunction<HashMap<String, QueryPool.LockType>>() {
                        @Override
                        public void prepare(RGraphQLObjectTypeField rGraphQLObjectTypeField, HashMap<String, QueryPool.LockType> prepare) {
                            if (prepare != null) {
                                prepare.forEach((resource, lockType) -> {
                                    ResourceProviderImpl.appendResource(resource, lockType, waitLockResources);
                                });

                                resultQueryPoolRequest[0] = true;
                            } else if (!resultQueryPoolRequest[0]) {
                                FieldConfiguration fieldConfiguration = (FieldConfiguration) rGraphQLObjectTypeField.configuration;
                                if (fieldConfiguration != null) {
                                    for (Class<? extends UnauthorizedContext> authorizedContext : fieldConfiguration.typeAuthContexts) {
                                        if (authorizedContext != UnauthorizedContext.class) {
                                            resultQueryPoolRequest[0] = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
            );
        } catch (GraphQLExecutorDataFetcherException exception) {
            if (exception.getCause() instanceof PlatformException) {
                throw (PlatformException) exception.getCause();
            } else {
                throw exception;
            }
        }
        queryPoolRequest = resultQueryPoolRequest[0];
    }

    public GraphQLExecutorPrepareImpl.PrepareDocumentRequest getPrepareDocumentRequest() {
        return prepareDocumentRequest;
    }

    public boolean isQueryPoolRequest() {
        return queryPoolRequest;
    }

    public HashMap<String, QueryPool.LockType> getWaitLockResources() {
        return waitLockResources;
    }


}
