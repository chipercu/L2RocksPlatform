package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;


@GraphQLTypeOutObject("app_config")
public class GQueryAppConfig {

	@GraphQLField
	@GraphQLDescription("Конфигурация платформы")
	public static Class<GQueryAppConfigCore> getCore() {
		return GQueryAppConfigCore.class;
	}
}
