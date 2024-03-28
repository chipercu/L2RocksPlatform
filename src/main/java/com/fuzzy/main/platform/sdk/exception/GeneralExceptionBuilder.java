package com.fuzzy.main.platform.sdk.exception;

import com.fuzzy.main.platform.exception.ExceptionFactory;
import com.fuzzy.main.platform.exception.GeneralExceptionFactory;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    public static final String INVALID_CREDENTIALS = "invalid_credentials";
    public static final String NOT_FOUND_DOMAIN_OBJECT_CODE = "not_found_domain_object";
    public static final String NOT_EMPTY_DOMAIN_OBJECT_CODE = "not_empty_domain_object";
    private static final String NOT_UNIQUE_VALUE_CODE = "not_unique_value";
    private static final String INVALID_VALUE_CODE = "invalid_value";
    public static final String ACCESS_DENIED_CODE = "access_denied";
    private static final String OBLIGATORY_PARAM = "obligatory_param";

    private GeneralExceptionBuilder() {
    }

    public static PlatformException buildDatabaseException(DatabaseException cause) {
        return EXCEPTION_FACTORY.build("database_error", cause);
    }

    public static PlatformException buildDatabaseException(DatabaseException cause, Map<String, Object> params) {
        return EXCEPTION_FACTORY.build("database_error", cause, params);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, Long id) {
        return EXCEPTION_FACTORY.build(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
            put("id", id);
        }});
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        StructEntity entity = Schema.getEntity(clazz);
        return EXCEPTION_FACTORY.build(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", entity.getName());
            put("field_name", entity.getField(fieldNumber).getName());
            put("field_value", fieldValue);
        }});
    }

    public static PlatformException buildDomainObjectAlreadyExistsException(Class<? extends DomainObject> clazz, Long id) {
        return EXCEPTION_FACTORY.build("domain_object_already_exists", new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
            put("id", id);
        }});
    }

    public static PlatformException buildNotEmptyDomainObjectException(Class<? extends DomainObject> clazz) {
        return EXCEPTION_FACTORY.build(NOT_EMPTY_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
        }});
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber) {
        StructEntity entity = Schema.getEntity(clazz);

        HashMap<String, Object> params = new HashMap<>();
        params.put("type", entity.getName());
        params.put("field_name", entity.getField(fieldNumber).getName());
        return params;
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        Map<String, Object> params = buildParams(clazz, fieldNumber);
        params.put("field_value", fieldValue);
        return params;
    }

    public static PlatformException buildInvalidCredentialsException() {
        return EXCEPTION_FACTORY.build(INVALID_CREDENTIALS);
    }

    public static PlatformException buildInvalidCredentialsException(String type, String name) {
        return EXCEPTION_FACTORY.build(INVALID_CREDENTIALS, Map.of("type", type, "name", name));
    }

    public static PlatformException buildInvalidJsonException() {
        return buildInvalidJsonException(null);
    }

    public static PlatformException buildGraphQLInvalidSyntaxException() {
        return EXCEPTION_FACTORY.build("graphql_invalid_syntax");
    }

    public static PlatformException buildGraphQLValidationException() {
        return buildGraphQLValidationException(null);
    }

    public static PlatformException buildGraphQLValidationException(String message) {
        return EXCEPTION_FACTORY.build("graphql_validation_error", message);
    }

    public static PlatformException buildInvalidJsonException(Throwable cause) {
        return EXCEPTION_FACTORY.build("invalid_json", cause);
    }

    public static PlatformException buildIllegalStateException(String message) {
        return EXCEPTION_FACTORY.build("illegal_state_exception", message);
    }

    public static PlatformException buildIOErrorException(IOException e) {
        return EXCEPTION_FACTORY.build("io_error", e);
    }

    public static PlatformException buildSecurityException(SecurityException e) {
        return EXCEPTION_FACTORY.build("security_exception", e);
    }

    public static PlatformException buildEmptyValueException(String fieldName) {
        return EXCEPTION_FACTORY.build("empty_value", Collections.singletonMap("fieldName", fieldName));
    }

    public static PlatformException buildEmptyValueException(Class<? extends DomainObject> clazz, int fieldNumber) {
        return EXCEPTION_FACTORY.build("empty_value", buildParams( clazz, fieldNumber));
    }

    public static PlatformException buildNotUniqueValueException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        return EXCEPTION_FACTORY.build(NOT_UNIQUE_VALUE_CODE, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static PlatformException buildNotUniqueValueException(String name, Object value) {
        return EXCEPTION_FACTORY.build(NOT_UNIQUE_VALUE_CODE, Collections.singletonMap(name, value));
    }

    public static PlatformException buildInvalidValueException(String fieldName) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("field_name", fieldName);
        }});
    }

    public static PlatformException buildInvalidValueExceptionWithCause(String cause) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("cause", cause);
        }});
    }

    public static PlatformException buildInvalidValueException(String fieldName, Serializable fieldValue) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("field_name", fieldName);
            put("field_value", fieldValue);
        }});
    }

    public static PlatformException buildInvalidValueException(String fieldName, Serializable fieldValue, String comment) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, comment, new HashMap<String, Object>() {{
            put("field_name", fieldName);
            put("field_value", fieldValue);
        }});
    }

    public static PlatformException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static PlatformException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue, String comment) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, comment, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static PlatformException buildUploadFileNotFoundException() {
        return EXCEPTION_FACTORY.build("upload_file_not_found");
    }

    public static PlatformException buildOnlyWebsocket() {
        return EXCEPTION_FACTORY.build("only_websocket");
    }

    public static PlatformException buildAccessDeniedException() {
        return EXCEPTION_FACTORY.build(ACCESS_DENIED_CODE);
    }

    public static PlatformException buildServerBusyException(String cause) {
        return EXCEPTION_FACTORY.build("server_busy", Collections.singletonMap("cause", cause));
    }

    public static PlatformException buildServerOverloadedException() {
        return EXCEPTION_FACTORY.build("server_overloaded");
    }

    public static PlatformException buildServerTimeoutException() {
        return EXCEPTION_FACTORY.build("server_timeout");
    }

    public static PlatformException buildServerShutsDownException() {
        return EXCEPTION_FACTORY.build("server_shuts_down");
    }

    public static PlatformException buildAuthAmbiguityException(String message) {
        return EXCEPTION_FACTORY.build("auth_ambiguity", message);
    }

    /**
     * @return Не найден обязательный параметр.
     */
    public static PlatformException buildNotFoundObligatoryParam(Class<? extends DomainObject> clazz, int fieldNumber) {
        return EXCEPTION_FACTORY.build(OBLIGATORY_PARAM, GeneralExceptionBuilder.buildParams(clazz, fieldNumber));
    }

    public static PlatformException buildNotFoundObligatoryParam(String fieldName) {
        return EXCEPTION_FACTORY.build(OBLIGATORY_PARAM, fieldName);
    }

}


