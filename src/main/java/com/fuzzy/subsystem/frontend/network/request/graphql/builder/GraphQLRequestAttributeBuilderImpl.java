package com.fuzzy.subsystem.frontend.network.request.graphql.builder;

import com.infomaximum.platform.component.frontend.request.graphql.builder.impl.attribute.GraphQLRequestAttributeBuilder;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.frontend.utils.HttpsRequestUtil;
import com.fuzzy.subsystems.utils.CertificateUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.security.cert.X509Certificate;
import java.util.HashMap;

public class GraphQLRequestAttributeBuilderImpl implements GraphQLRequestAttributeBuilder {


    @Override
    public HashMap<String, String[]> build(HttpServletRequest request) throws PlatformException {
        HashMap<String, String[]> attributes = null;

        X509Certificate[] certificates = HttpsRequestUtil.getCertificates(request);
        if (certificates != null) {
            attributes = new HashMap<>();

            String[] certificateApiKeys = new String[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                certificateApiKeys[i] = CertificateUtil.getThumbprint(certificates[i]);
            }
            attributes.put(HttpsRequestUtil.ATTRIBUTE_CERTIFICATE_API_KEYS, certificateApiKeys);
        }

        return attributes;
    }
}
