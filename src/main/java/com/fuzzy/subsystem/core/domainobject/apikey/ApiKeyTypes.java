package com.fuzzy.subsystem.core.domainobject.apikey;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

public class ApiKeyTypes {

	public static final String AD_SUBSYSTEM_UUID_TEMP = "com.fuzzy.subsystem.activedirectory";

	public static final ApiKeyType NONE = new ApiKeyType("none", Subsystems.UUID);
	public static final ApiKeyType CERTIFICATE = new ApiKeyType("certificate", Subsystems.UUID);
	public static final ApiKeyType AD = new ApiKeyType("ad", AD_SUBSYSTEM_UUID_TEMP);

	public static ApiKeyType instanceOf(String type, String subsystemUuid) throws PlatformException {
		ApiKeyType result = NONE.isSame(type, subsystemUuid) ? NONE :
				CERTIFICATE.isSame(type, subsystemUuid) ? CERTIFICATE :
						AD.isSame(type, AD_SUBSYSTEM_UUID_TEMP) ? AD : null;
		if (result == null) {
			throw CoreExceptionBuilder.buildUnsupportedApiKeyTypeException();
		}
		return result;
	}
}
