package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public enum ChangePasswordCause implements RemoteObject {

	EMPLOYEE_UPDATE(0),

	CHANGE_EXPIRATION_PASSWORD(1),

	RESET_PASSWORD(2),

	SET_PASSWORD_BY_INVITATION(3);

	private final int id;

	ChangePasswordCause(int id) {
		this.id = id;
	}

	public static ChangePasswordCause get(long id) {
		for (ChangePasswordCause item : ChangePasswordCause.values()) {
			if (item.id == id) {
				return item;
			}
		}
		return null;
	}
}
