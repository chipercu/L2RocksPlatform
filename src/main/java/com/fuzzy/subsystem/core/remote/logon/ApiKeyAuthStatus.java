package com.fuzzy.subsystem.core.remote.logon;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("api_key_auth_status")
public enum  ApiKeyAuthStatus implements RemoteObject {

	SUCCESS(0),

	FAIL(1);

	private final int id;

	ApiKeyAuthStatus(int id) {
		this.id = id;
	}

	public static ApiKeyAuthStatus get(long id) {
		for (ApiKeyAuthStatus item : ApiKeyAuthStatus.values()) {
			if (item.id == id) {
				return item;
			}
		}
		return null;
	}
}
