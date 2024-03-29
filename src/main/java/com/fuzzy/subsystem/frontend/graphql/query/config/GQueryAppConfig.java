package com.fuzzy.subsystem.frontend.graphql.query.config;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("app_config")
public class GQueryAppConfig {

	@GraphQLField
	public static Class<GQueryAppConfigFrontend> getFrontend() {
		return GQueryAppConfigFrontend.class;
	}
}
