package com.fuzzy.subsystem.frontend.service.session;

import java.time.Instant;
import java.util.Objects;

public class SessionTimeout {

	private final String uuid;
	private Instant timeout;

	public SessionTimeout(String uuid, Instant timeout) {
		this.uuid = uuid;
		this.timeout = timeout;
	}

	public String getUuid() {
		return uuid;
	}

	public Instant getTimeout() {
		return timeout;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SessionTimeout that = (SessionTimeout) o;
		return Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}