package com.fuzzy.subsystem.frontend.service.session;

import com.google.common.hash.Hashing;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public abstract class Session {

	public final String uuid;
	private final String componentUuid;
	private final String authenticationType;
	private final Map<String, String> params;
	private volatile Instant accessTime;

	public Session(@NonNull String uuid,
				   @NonNull String componentUuid,
				   @NonNull String authenticationType,
				   @NonNull Map<String, String> params) {
		this.uuid = uuid;
		this.componentUuid = componentUuid;
		this.authenticationType = authenticationType;
		this.accessTime = Instant.now();
		this.params = Collections.unmodifiableMap(params);
	}

	public String getComponentUuid() {
		return componentUuid;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void updateAccessTime() {
		this.accessTime = Instant.now();
	}

	public Instant getAccessTime() {
		return accessTime;
	}

	public Map<String, String> getParams() {
		return this.params;
	}

	public static String getHash(String sessionId) {
		return Hashing.sha256()
				.hashString(sessionId, StandardCharsets.UTF_8)
				.toString();
	}

	public abstract SyslogStructDataTarget getSysLogDataTarget();

}
