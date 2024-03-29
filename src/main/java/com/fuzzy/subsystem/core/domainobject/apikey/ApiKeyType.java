package com.fuzzy.subsystem.core.domainobject.apikey;

public class ApiKeyType {

	private final String type;
	private final String subsystemUuid;

	ApiKeyType(String type, String subsystemUuid) {
		this.type = type;
		this.subsystemUuid = subsystemUuid;
	}

	public String getType() {
		return type;
	}

	public String getSubsystemUuid() {
		return subsystemUuid;
	}

	public boolean isSame(String type, String subsystemUuid) {
		return this.type.equals(type) && this.subsystemUuid.equals(subsystemUuid);
	}

	public boolean isSame(ApiKeyReadable apiKeyReadable) {
		return this.type.equals(apiKeyReadable.getType()) && this.subsystemUuid.equals(apiKeyReadable.getSubsystemUuid());
	}
}
