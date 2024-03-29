package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.config.ComplexPassword;

@GraphQLTypeOutObject("complex_password_output")
public class GComplexPasswordOutput implements RemoteObject {

	private final ComplexPassword complexPassword;

	public GComplexPasswordOutput(ComplexPassword complexPassword) {
		this.complexPassword = complexPassword;
	}

	@GraphQLField
	@GraphQLAuthControl(UnauthorizedContext.class)
	@GraphQLDescription("Минимальная длина пароля")
	public int getMinPasswordLength() {
		return complexPassword.getMinPasswordLength();
	}
}