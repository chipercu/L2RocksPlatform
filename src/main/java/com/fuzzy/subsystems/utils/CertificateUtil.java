package com.fuzzy.subsystems.utils;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil {

    private final static String DEFAULT_THUMBPRINT_ALGORITHM = "SHA-256";

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

    public static String getThumbprint(X509Certificate cert) throws PlatformException {
		try {
			MessageDigest md = MessageDigest.getInstance(DEFAULT_THUMBPRINT_ALGORITHM);
			byte[] der = cert.getEncoded();
			md.update(der);
			byte[] digest = md.digest();
			String digestHex = HexConverter.bytesToHex(digest);
			return digestHex.toUpperCase();
		} catch (NoSuchAlgorithmException | CertificateEncodingException e) {
			throw GeneralExceptionBuilder.buildInvalidCertificateException(e);
		}
	}
}
