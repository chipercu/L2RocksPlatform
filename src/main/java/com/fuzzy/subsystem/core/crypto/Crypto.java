package com.fuzzy.subsystem.core.crypto;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.FileUtils;
import com.fuzzy.utils.Random;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Crypto {

	private final Charset charset = StandardCharsets.UTF_8;
	private final int saltLength = 128;
	private final Path keyPath;
	private volatile byte[] secretKey;

	public Crypto(Path keyPath) throws PlatformException {
		this.keyPath = keyPath;
		if (keyPath.getNameCount() == 0) {
			throw GeneralExceptionBuilder.buildInvalidPathException(keyPath);
		}
	}

	public synchronized void createKey() throws PlatformException {
		FileUtils.ensureDirectories(this.keyPath.getParent());
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			keyGen.init(128, sr); // must be equal to 128, 192 or 256
			byte[] secretKey = keyGen.generateKey().getEncoded();
			FileUtils.saveToFile(secretKey, this.keyPath);
			this.secretKey = secretKey;
		} catch (NoSuchAlgorithmException e) {
			throw GeneralExceptionBuilder.buildGeneralSecurityException(e);
		}
	}

	public synchronized void createKeyIfNotExists() throws PlatformException {
		if (!Files.exists(keyPath)) {
			createKey();
		}
	}

	public byte[] encrypt(byte[] value) throws PlatformException {
		if (value == null) {
			return null;
		}
		byte[] salt = new byte[this.saltLength];
		Random.secureRandom.nextBytes(salt);
		value = ArrayUtils.addAll(value, salt);
		return cryptoExec(value, Cipher.ENCRYPT_MODE);
	}

	public byte[] encrypt(String value) throws PlatformException {
		if (value == null) {
			return null;
		}
		byte[] bytes = value.getBytes(this.charset);
		return encrypt(bytes);
	}

	public byte[] decrypt(byte[] value) throws PlatformException {
		if (value == null) {
			return null;
		}
		byte[] result = cryptoExec(value, Cipher.DECRYPT_MODE);
		result = Arrays.copyOfRange(result, 0, result.length - saltLength);
		return result;
	}

	public String decryptAsString(byte[] value) throws PlatformException {
		if (value == null) {
			return null;
		}
		return new String(decrypt(value), charset);
	}

	private byte[] cryptoExec(byte[] value, int encryptMode) throws PlatformException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(encryptMode, secretKeySpec);
			return cipher.doFinal(value);
		} catch (GeneralSecurityException e) {
			throw GeneralExceptionBuilder.buildGeneralSecurityException(e);
		}
	}

	private byte[] getSecretKey() throws PlatformException {
		if (this.secretKey == null) {
			synchronized (this) {
				if (this.secretKey == null) {
					this.secretKey = FileUtils.readBytesFromFile(this.keyPath);
				}
			}
		}
		return this.secretKey;
	}
}
