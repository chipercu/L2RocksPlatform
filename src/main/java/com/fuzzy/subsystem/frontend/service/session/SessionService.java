package com.fuzzy.subsystem.frontend.service.session;

import java.util.concurrent.ConcurrentMap;

public interface SessionService {
	ConcurrentMap<String, ? extends Session> getSessionsAsMap();

	long getSessionTimeout();
}
