package com.fuzzy.subsystem.core.crypto;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.FileUtils;
import com.infomaximum.utils.Random;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

public class CryptoPassword {

    private static final String MASK = "Jvj8r1D2YRqxxxtQiLo5f6dF+U7Nf1zt8HAZRsi/3+Y=";
    private final byte[] crypto_key;
    private final byte[] secretKey;

    private final Charset charset = StandardCharsets.UTF_8;
    private final int saltLength = 128;

    public CryptoPassword(Path keyPath) throws PlatformException {
        if (keyPath.getNameCount() == 0) {
            throw GeneralExceptionBuilder.buildInvalidPathException(keyPath);
        }
        this.secretKey = FileUtils.readBytesFromFile(keyPath);
        this.crypto_key = formCustomKey();
    }

    private byte[] encrypt(byte[] value) throws PlatformException {
        if (value == null) {
            return null;
        }
        byte[] salt = new byte[this.saltLength];
        Random.secureRandom.nextBytes(salt);
        value = ArrayUtils.addAll(value, salt);
        return cryptoExec(value, Cipher.ENCRYPT_MODE);
    }

    private byte[] decrypt(byte[] value) throws PlatformException {
        if (value == null) {
            return null;
        }
        byte[] result = cryptoExec(value, Cipher.DECRYPT_MODE);
        result = Arrays.copyOfRange(result, 0, result.length - saltLength);
        return result;
    }

    private byte[] cryptoExec(byte[] value, int encryptMode) throws PlatformException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(crypto_key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(encryptMode, secretKeySpec);
            return cipher.doFinal(value);
        } catch (GeneralSecurityException e) {
            throw GeneralExceptionBuilder.buildGeneralSecurityException(e);
        }
    }

    public String encrypt(String value) throws PlatformException {
        byte[] bytes = value.getBytes(charset);
        byte[] encrypted = encrypt(bytes);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decryptBase64String(String base64String) throws PlatformException {
        byte[] encrypted = Base64.getDecoder().decode(base64String);
        return new String(decrypt(encrypted));
    }

    private byte[] formCustomKey() {
        byte[] result = new byte[secretKey.length];
        byte[] maskBytes = Base64.getDecoder().decode(MASK);
        for (int i = 0; i < secretKey.length; i++) {
            result[i] = (byte) (secretKey[i] & maskBytes[i]);
        }
        return result;
    }
}
