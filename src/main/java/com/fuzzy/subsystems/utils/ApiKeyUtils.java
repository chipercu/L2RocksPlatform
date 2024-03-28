package com.fuzzy.subsystems.utils;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

public class ApiKeyUtils {

    public static void advanceValidationApiKey(String value) throws PlatformException{
        if (value == null || value.length() <= 8) {
            throw GeneralExceptionBuilder.buildIllegalApiKeyException();
        }
    }

	public static String concealmentApiKey(String value) {
		if (value == null || value.length() <= 8) {
			throw new IllegalArgumentException();
		}
		StringBuilder result = new StringBuilder(value.length());
		result.append(value, 0, 4);
		for (int i = 0; i < value.length() - 8; i++) {
			result.append('*');
		}
		result.append(value, value.length() - 4, value.length());
		return result.toString();
	}

}
