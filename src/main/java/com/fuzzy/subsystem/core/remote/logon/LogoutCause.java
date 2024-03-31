package com.fuzzy.subsystem.core.remote.logon;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public enum LogoutCause implements RemoteObject {

	TIMEOUT(0),

	MANUAL(1),

	FORCE(2);

	private final int id;

	LogoutCause(int id) {
		this.id = id;
	}

	public static LogoutCause get(long id) {
		for (LogoutCause item : LogoutCause.values()) {
			if (item.id == id) {
				return item;
			}
		}
		return null;
	}
}
