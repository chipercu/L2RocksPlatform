package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.database.utils.BaseEnum;

@GraphQLTypeOutObject("filter_operation")
public enum GFilterOperation implements RemoteObject, BaseEnum {

	INCLUDE(1),
	EXCLUDE(2),
	EMPTY(3),
	NONEMPTY(4);

	private final int id;

	GFilterOperation(int id) {
		this.id = id;
	}

	@Override
	public int intValue() {
		return id;
	}

	public static GFilterOperation get(long id) {
		for (GFilterOperation operation : GFilterOperation.values()) {
			if (operation.intValue() == id) {
				return operation;
			}
		}
		return null;
	}
}
