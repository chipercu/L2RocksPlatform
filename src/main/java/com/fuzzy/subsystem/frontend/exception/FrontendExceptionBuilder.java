package com.fuzzy.subsystem.frontend.exception;

import com.fuzzy.main.platform.exception.ExceptionFactory;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.exception.PlatformExceptionFactory;
import com.fuzzy.subsystem.frontend.FrontendSubsystemConsts;

import java.util.HashMap;

public class FrontendExceptionBuilder {

	private static final ExceptionFactory EXCEPTION_FACTORY = new PlatformExceptionFactory(FrontendSubsystemConsts.UUID);

	public static PlatformException buildCertificateValidationException(Throwable e) {
		return EXCEPTION_FACTORY.build("certificate_validation_error", e);
	}

	public static PlatformException buildDeniedLogonInServiceModeException(String message) {
		return EXCEPTION_FACTORY.build("denied_logon_in_service_mode", new HashMap<String, Object>() {{
			put("message", message);
		}});
	}

	public static PlatformException buildServiceModeActivatedException() {
		return EXCEPTION_FACTORY.build("service_mode_activated");
	}
}
