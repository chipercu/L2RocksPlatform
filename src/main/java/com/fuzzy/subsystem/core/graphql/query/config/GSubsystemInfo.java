package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;


@GraphQLTypeOutObject("subsystem")
public class GSubsystemInfo implements RemoteObject {

	private final String uuid;
	private final String version;

	public GSubsystemInfo(String uuid, String version) {
		this.uuid = uuid;
		this.version = version;
	}

	@GraphQLField
	@GraphQLAuthControl(UnauthorizedContext.class)
	@GraphQLDescription("Идентификатор модуля")
	public String getUuid() {
		return uuid;
	}

	@GraphQLField
	@GraphQLAuthControl(UnauthorizedContext.class)
	@GraphQLDescription("Версия модуля")
	public String getVersion() {
		return version;
	}
}
