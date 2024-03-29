package com.fuzzy.subsystem.core.config;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("logon_type")
public enum LogonType implements RemoteObject, BaseEnum {

	EMAIL(1),

	LOGIN(2);

	private final int id;

	LogonType(int id) {
		this.id = id;
	}

	@Override
	public int intValue() {
		return id;
	}

	public static LogonType get(String name) {
		for (LogonType type : LogonType.values()) {
			if (type.name().toLowerCase().equals(name)) {
				return type;
			}
		}
		return null;
	}

	public static LogonType get(long id) {
		for (LogonType type : LogonType.values()) {
			if (type.intValue() == id) {
				return type;
			}
		}
		return null;
	}
}
