package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("mutation_app_config")
public class GMutationAppConfig {

	@GraphQLField
	@GraphQLDescription("Модификация платформы")
	public static Class<GMutationAppConfigCore> getCore() {
		return GMutationAppConfigCore.class;
	}
}
