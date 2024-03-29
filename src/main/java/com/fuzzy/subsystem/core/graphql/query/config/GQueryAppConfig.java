package com.fuzzy.subsystem.core.graphql.query.config;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;


@GraphQLTypeOutObject("app_config")
public class GQueryAppConfig {

	@GraphQLField
	@GraphQLDescription("Конфигурация платформы")
	public static Class<GQueryAppConfigCore> getCore() {
		return GQueryAppConfigCore.class;
	}
}
