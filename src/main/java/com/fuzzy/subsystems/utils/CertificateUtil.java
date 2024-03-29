package com.fuzzy.subsystems.utils;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil {

    private final static String THUMBPRINT_ALGORITHM_SHA256 = "SHA-256";
	private final static String THUMBPRINT_ALGORITHM_SHA1 = "SHA-1";
	private final static String DEFAULT_THUMBPRINT_ALGORITHM = THUMBPRINT_ALGORITHM_SHA256;

    public static X509Certificate buildCertificate(InputStream inputStream) throws PlatformException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw GeneralExceptionBuilder.buildInvalidCertificateException(e);
        }
    }

    //TODO Возможно нигде не используется удалить после 01.01.2025
//    public static String getCN(X509Certificate cert) throws PlatformException {
//        String dn = cert.getSubjectX500Principal().getName();
//        try {
//            LdapName ldapDN = new LdapName(dn);
//            return (String) ldapDN.getRdns().stream().filter(rdn -> rdn.getType().equals("CN")).map(Rdn::getValue).findAny().orElse(null);
//        } catch (InvalidNameException e) {
//            throw FrontendExceptionBuilder.buildCertificateValidationException(e);
//        }
//    }

	public static String getThumbprint(X509Certificate cert, String algorithm) throws PlatformException {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] der = cert.getEncoded();
			md.update(der);
			byte[] digest = md.digest();
			String digestHex = HexConverter.bytesToHex(digest);
			return digestHex.toUpperCase();
		} catch (NoSuchAlgorithmException | CertificateEncodingException e) {
			throw GeneralExceptionBuilder.buildInvalidCertificateException(e);
		}
	}

    public static String getThumbprint(X509Certificate cert) throws PlatformException {
		return getThumbprint(cert, DEFAULT_THUMBPRINT_ALGORITHM);
	}

	public static String getThumbprintSHA1(X509Certificate cert) throws PlatformException {
		return getThumbprint(cert, THUMBPRINT_ALGORITHM_SHA1);
	}
}
