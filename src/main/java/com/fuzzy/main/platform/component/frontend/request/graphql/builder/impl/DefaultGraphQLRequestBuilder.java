package com.fuzzy.main.platform.component.frontend.request.graphql.builder.impl;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.component.frontend.request.graphql.GraphQLRequest;
import com.fuzzy.main.platform.component.frontend.request.graphql.builder.ClearUploadFiles;
import com.fuzzy.main.platform.component.frontend.request.graphql.builder.GraphQLRequestBuilder;
import com.fuzzy.main.platform.component.frontend.request.graphql.builder.impl.attribute.GraphQLRequestAttributeBuilder;
import com.fuzzy.main.platform.component.frontend.request.graphql.builder.impl.attribute.GraphQLRequestAttributeBuilderEmpty;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.eclipse.jetty.http.BadMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class DefaultGraphQLRequestBuilder implements GraphQLRequestBuilder {

    private final static Logger log = LoggerFactory.getLogger(DefaultGraphQLRequestBuilder.class);

    private static final String QUERY_PARAM = "query";
    private static final String VARIABLES_PARAM = "variables";
    private static final String OPERATION_NAME = "operationName";

    private final FrontendMultipartSource frontendMultipartSource;
    private final ClearUploadFiles clearUploadFiles;

    private final GraphQLRequestAttributeBuilder attributeBuilder;

    public DefaultGraphQLRequestBuilder(
            FrontendMultipartSource frontendMultipartSource,
            GraphQLRequestAttributeBuilder attributeBuilder
    ) {
        this.frontendMultipartSource = frontendMultipartSource;
        this.clearUploadFiles = new ClearUploadFilesImpl(frontendMultipartSource);

        this.attributeBuilder = attributeBuilder;
    }

    @Override
    public GraphQLRequest build(HttpServletRequest request) throws PlatformException {
        ArrayList<GRequestHttp.UploadFile> uploadFiles = null;//Если есть сохраняем ссылки на загруженные файлы

        String rawRemoteAddress = request.getRemoteAddr();
        String endRemoteAddress = request.getHeader("X-Real-IP");
        if (endRemoteAddress == null) {
            endRemoteAddress = rawRemoteAddress;
        }
        GRequest.RemoteAddress remoteAddress = new GRequest.RemoteAddress(rawRemoteAddress, endRemoteAddress);

        String xTraceId = request.getHeader("X-Trace-Id");

        HashMap<String, String[]> parameters = new HashMap<>();

        try {
            //Собираем параметры
            String query = request.getParameter(QUERY_PARAM);
            HashMap<String, Serializable> queryVariables = null;
            String operationName = request.getParameter(OPERATION_NAME);

            String variablesJson = request.getParameter(VARIABLES_PARAM);
            if (variablesJson != null) {
                JSONObject variables = parseJSONObject(variablesJson);
                if (variables != null) {
                    queryVariables = new HashMap<>((Map) variables);
                }
            }

            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                parameters.put(parameterName, request.getParameterValues(parameterName));
            }

            if (request instanceof StandardMultipartHttpServletRequest multipartRequest) {//Проверяем возможно это Multipart request
                Map<String, String[]> multipartParameters = multipartRequest.getParameterMap();
                String[] queryArray = multipartParameters.get(QUERY_PARAM);
                if (queryArray != null && queryArray.length > 0) {
                    query = queryArray[0];
                }

                String[] variablesJsonArray = multipartParameters.get(VARIABLES_PARAM);
                if (variablesJsonArray != null && variablesJsonArray.length > 0) {
                    JSONObject variables = parseJSONObject(variablesJsonArray[0]);
                    if (variables != null) {
                        queryVariables = new HashMap<>((Map) variables);
                    }
                }

                String[] operationNameArray = multipartParameters.get(OPERATION_NAME);
                if (operationNameArray != null && operationNameArray.length > 0) {
                    operationName = operationNameArray[0];
                }

                multipartParameters.forEach((key, values) -> parameters.put(key, values));

                MultiValueMap<String, MultipartFile> multipartFiles = multipartRequest.getMultiFileMap();
                if (multipartFiles != null && !multipartFiles.isEmpty()) {
                    uploadFiles = new ArrayList<>();

                    for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
                        for (MultipartFile multipartFile : entry.getValue()) {
                            uploadFiles.add(new GRequestHttp.UploadFile(
                                    entry.getKey(),
                                    multipartFile.getOriginalFilename(),
                                    multipartFile.getContentType(),
                                    frontendMultipartSource.put(multipartFile),
                                    multipartFile.getSize()
                            ));
                        }
                    }
                }
            } else {//Ищем POST параметры
                BufferedReader reader = null;
                try {
                    reader = request.getReader();
                } catch (IllegalStateException ignore) {
                }
                if (reader != null) {
                    JSONObject dataPostVariables = parseJSONObject(request.getReader());
                    if (dataPostVariables != null) {
                        if (dataPostVariables.containsKey(QUERY_PARAM)) {
                            query = dataPostVariables.getAsString(QUERY_PARAM);
                        }

                        Object variables = dataPostVariables.get(VARIABLES_PARAM);
                        if (variables != null && variables instanceof Map) {
                            queryVariables = new HashMap<>((Map) variables);
                        }

                        if (dataPostVariables.containsKey(OPERATION_NAME)) {
                            operationName = dataPostVariables.getAsString(OPERATION_NAME);
                        }

                        dataPostVariables.forEach((key, value) -> {
                            if (value instanceof String) {
                                parameters.put(key, new String[]{ (String) value });
                            }
                        });
                    }
                }
            }

            if (query == null || query.isBlank()) {
                throw GeneralExceptionBuilder.buildEmptyValueException(QUERY_PARAM);
            }

            HashMap<String, String[]> attributes = attributeBuilder.build(request);

            GRequestHttp gRequest = new GRequestHttp(
                    Instant.now(),
                    remoteAddress,
                    query, queryVariables != null ? queryVariables : new HashMap<>(), operationName,
                    xTraceId,
                    parameters,
                    attributes,
                    request.getCookies(),
                    uploadFiles
            );

            return new GraphQLRequest(
                    gRequest,
                    clearUploadFiles
            );
        } catch (BadMessageException | IOException pe) {
            clearUploadFiles.clear(uploadFiles);
            throw GeneralExceptionBuilder.buildInvalidJsonException(pe);
        } catch (Throwable t) {
            clearUploadFiles.clear(uploadFiles);
            throw t;
        }
    }

    private static JSONObject parseJSONObject(Reader in) throws PlatformException {
        try {
            Object parseData = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
            return castToJSONObject(parseData);
        } catch (ParseException e) {
            throw GeneralExceptionBuilder.buildInvalidJsonException(e);
        }
    }

    private static JSONObject parseJSONObject(String in) throws PlatformException {
        try {
            Object parseData = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
            return castToJSONObject(parseData);
        } catch (ParseException e) {
            throw GeneralExceptionBuilder.buildInvalidJsonException(e);
        }
    }

    private static JSONObject castToJSONObject(Object obj) throws PlatformException {
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else if (obj instanceof String) {
            if (((String) obj).isEmpty()) {
                return null;
            }
        }

        throw GeneralExceptionBuilder.buildInvalidJsonException();
    }


    public static class Builder extends GraphQLRequestBuilder.Builder {

        private GraphQLRequestAttributeBuilder attributeBuilder;

        public Builder() {
            attributeBuilder = new GraphQLRequestAttributeBuilderEmpty();
        }

        public Builder withAttributeBuilder(GraphQLRequestAttributeBuilder attributeBuilder) {
            this.attributeBuilder = attributeBuilder;
            return this;
        }

        public GraphQLRequestBuilder build(
                FrontendMultipartSource frontendMultipartSource
        ) {
            return new DefaultGraphQLRequestBuilder(frontendMultipartSource, attributeBuilder);
        }

    }
}
