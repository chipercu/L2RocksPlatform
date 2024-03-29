package com.fuzzy.subsystem.frontend.remote.session;

import com.fuzzy.subsystem.frontend.service.session.SessionEmployee;

public class SessionEmployeeRemoteAdapter extends SessionRemoteAdapter {

	private final long employeeId;

	public SessionEmployeeRemoteAdapter(SessionEmployee session) {
		super(session);
		employeeId = session.employeeId;
	}

	public long getEmployeeId() {
		return employeeId;
	}
}
