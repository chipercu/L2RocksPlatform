package com.fuzzy.cluster.graphql.remote.graphql.executor;

import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.cluster.graphql.struct.ContextRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kris on 02.11.16.
 */
public interface RControllerGraphQLExecutor extends RController {

	public ArrayList<RGraphQLType> getGraphQLTypes() throws Exception;

    public Serializable prepare(String requestItemKey, String graphQLTypeName, String graphQLTypeFieldName, HashMap<String, Serializable> arguments, ContextRequest context) throws Exception;

    public Serializable executePrepare(String keyFieldRequest, RemoteObject source, ContextRequest context) throws Exception;

    public Serializable execute(RemoteObject source, String graphQLTypeName, String graphQLTypeFieldName, HashMap<String, Serializable> arguments, ContextRequest context) throws Exception;
}
