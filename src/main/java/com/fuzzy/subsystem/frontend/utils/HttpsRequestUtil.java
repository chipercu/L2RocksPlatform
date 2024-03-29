package com.fuzzy.subsystem.frontend.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.security.cert.X509Certificate;

public class HttpsRequestUtil {

	public static final String ATTRIBUTE_CERTIFICATE_API_KEYS = "certificate_api_keys";

	public static X509Certificate[] getCertificates(HttpServletRequest request) {
		return ((X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate"));
	}
}
