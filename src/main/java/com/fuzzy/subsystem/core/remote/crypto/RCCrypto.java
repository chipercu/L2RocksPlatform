package com.fuzzy.subsystem.core.remote.crypto;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCCrypto extends QueryRemoteController {

	byte[] encrypt(byte[] value, ContextTransaction context) throws PlatformException;

	byte[] encrypt(String value, ContextTransaction context) throws PlatformException;

	byte[] decrypt(byte[] value, ContextTransaction context) throws PlatformException;

	String decryptAsString(byte[] value, ContextTransaction context) throws PlatformException;
}
