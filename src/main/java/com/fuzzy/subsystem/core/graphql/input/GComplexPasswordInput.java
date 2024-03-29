package com.fuzzy.subsystem.core.graphql.input;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("complex_password_input")
public class GComplexPasswordInput implements RemoteObject {

	private final int minPasswordLength;

	public GComplexPasswordInput(
			@GraphQLDescription("Минимальная длина пароля")
			@NonNull @GraphQLName("min_password_length") Integer minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}

	public int getMinPasswordLength() {
		return minPasswordLength;
	}
}
