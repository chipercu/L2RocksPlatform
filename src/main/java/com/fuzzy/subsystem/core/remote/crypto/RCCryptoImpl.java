package com.fuzzy.subsystem.core.remote.crypto;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.crypto.Crypto;
import com.fuzzy.subsystems.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class RCCryptoImpl extends AbstractQueryRController<CoreSubsystem> implements RCCrypto {

	private final static Logger logger = LoggerFactory.getLogger(RCCryptoImpl.class);

	private static Crypto crypto;

	public static void initCrypto(Path keyPath) throws PlatformException {
		crypto = new Crypto(keyPath);
	}

	public static void createSecretKeyIfNotExists() throws PlatformException {
		crypto.createKeyIfNotExists();
	}

	public RCCryptoImpl(CoreSubsystem component, ResourceProvider resources) {
		super(component, resources);
	}

	@Override
	public byte[] encrypt(byte[] value, ContextTransaction context) {
		return exec(value, crypto::encrypt);
	}

	@Override
	public byte[] encrypt(String value, ContextTransaction context) {
		return exec(value, crypto::encrypt);
	}

	@Override
	public byte[] decrypt(byte[] value, ContextTransaction context) {
		return exec(value, crypto::decrypt);
	}

	@Override
	public String decryptAsString(byte[] value, ContextTransaction context) {
		return exec(value, crypto::decryptAsString);
	}

	private <T, R> R exec(T value, Function<T, R> function) {
		try {
			return function.apply(value);
		} catch (PlatformException e) {
			if (e.getCause() instanceof GeneralSecurityException) {
				logger.error("crypto_error", e.getCause());
				return null;
			} else {
				throw new RuntimeException(e);
			}
		}
	}
}
