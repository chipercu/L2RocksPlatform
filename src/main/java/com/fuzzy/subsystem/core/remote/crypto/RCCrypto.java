package com.fuzzy.subsystem.core.remote.crypto;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCCrypto extends QueryRemoteController {

	byte[] encrypt(byte[] value, ContextTransaction context) throws PlatformException;

	byte[] encrypt(String value, ContextTransaction context) throws PlatformException;

	byte[] decrypt(byte[] value, ContextTransaction context) throws PlatformException;

	String decryptAsString(byte[] value, ContextTransaction context) throws PlatformException;
}
