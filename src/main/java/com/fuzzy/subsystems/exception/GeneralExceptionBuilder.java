package com.fuzzy.subsystems.exception;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.exception.ExceptionFactory;
import com.infomaximum.platform.exception.GeneralExceptionFactory;
import com.infomaximum.platform.exception.PlatformException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    private static final String INVALID_CERTIFICATE_CODE = "invalid_certificate";
    public static final String NOT_FOUND_DOMAIN_OBJECT_CODE =
            com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.NOT_FOUND_DOMAIN_OBJECT_CODE;
    public static final String ACCESS_DENIED_CODE =
            com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.ACCESS_DENIED_CODE;
    public static final String NOT_EMPTY_DOMAIN_OBJECT_CODE =
            com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.NOT_EMPTY_DOMAIN_OBJECT_CODE;

    private GeneralExceptionBuilder() {
    }

    public static PlatformException buildDatabaseException(DatabaseException cause) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildDatabaseException(cause);
    }

    public static PlatformException buildDatabaseException(DatabaseException cause, Map<String, Object> params) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildDatabaseException(cause, params);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, Long id) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotFoundDomainObjectException(clazz, id);
    }

    public static PlatformException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotFoundDomainObjectException(clazz, fieldNumber, fieldValue);
    }

    public static PlatformException buildDomainObjectAlreadyExistsException(Class<? extends DomainObject> clazz, Long id) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildDomainObjectAlreadyExistsException(clazz, id);
    }

    public static PlatformException buildNotEmptyDomainObjectException(Class<? extends DomainObject> clazz) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotEmptyDomainObjectException(clazz);
    }

    public static PlatformException buildIncorrectMailTemplate() {
        return EXCEPTION_FACTORY.build("incorrect_mail_template");
    }

    public static PlatformException buildHierarchyException(Class<? extends DomainObject> clazz, int fieldNumber, Long fieldValue) {
        return EXCEPTION_FACTORY.build("hierarchy_error", buildParams(clazz, fieldNumber, fieldValue));
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildParams(clazz, fieldNumber);
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildParams(clazz, fieldNumber, fieldValue);
    }

    public static PlatformException buildInvalidCredentialsException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidCredentialsException();
    }

    public static PlatformException buildInvalidCredentialsException(String type, String name) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidCredentialsException(type, name);
    }

    public static PlatformException buildInvalidJsonException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidJsonException();
    }

    public static PlatformException buildGraphQLInvalidSyntaxException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildGraphQLInvalidSyntaxException();
    }

    public static PlatformException buildGraphQLValidationException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildGraphQLValidationException();
    }

    public static PlatformException buildGraphQLValidationException(String message) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildGraphQLValidationException(message);
    }

    public static PlatformException buildInvalidJsonException(Throwable cause) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidJsonException(cause);
    }

    public static PlatformException buildIllegalStateException(String message) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildIllegalStateException(message);
    }

    public static PlatformException buildIOErrorException(IOException e) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildIOErrorException(e);
    }

    public static PlatformException buildSecurityException(SecurityException e) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildSecurityException(e);
    }

    public static PlatformException buildEmptyValueException(String fieldName) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildEmptyValueException(fieldName);
    }

    public static PlatformException buildEmptyValueException(Class<? extends DomainObject> clazz, int fieldNumber) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildEmptyValueException(clazz, fieldNumber);
    }

    public static PlatformException buildNotUniqueValueException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotUniqueValueException(clazz, fieldNumber, fieldValue);
    }

    public static PlatformException buildNotUniqueValueException(String name, Object value) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotUniqueValueException(name, value);
    }

    public static PlatformException buildInvalidValueException(String fieldName) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueException(fieldName);
    }

    public static PlatformException buildInvalidValueExceptionWithCause(String cause) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueExceptionWithCause(cause);
    }

    public static PlatformException buildInvalidValueException(String fieldName, Serializable fieldValue) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueException(fieldName, fieldValue);
    }

    public static PlatformException buildInvalidValueException(String fieldName, Serializable fieldValue, String comment) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueException(fieldName, fieldValue, comment);
    }

    public static PlatformException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueException(clazz, fieldNumber, fieldValue);
    }

    public static PlatformException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue, String comment) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildInvalidValueException(clazz, fieldNumber, fieldValue, comment);
    }

    public static PlatformException buildAccessDeniedException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildAccessDeniedException();
    }

    public static PlatformException buildServerBusyException(String cause) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildServerBusyException(cause);
    }

    public static PlatformException buildServerOverloadedException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildServerOverloadedException();
    }

    public static PlatformException buildServerTimeoutException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildServerTimeoutException();
    }

    public static PlatformException buildServerShutsDownException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildServerShutsDownException();
    }

    public static PlatformException buildPasswordRecoveryLinkExpired() {
        return EXCEPTION_FACTORY.build("password_recovery_link_expired");
    }

    public static PlatformException buildNotAccessSendEventsException(String cause) {
        return EXCEPTION_FACTORY.build("not_access_send_events_exception", cause);
    }

    public static PlatformException buildInvitationLinkExpired() {
        return EXCEPTION_FACTORY.build("invitation_link_expired");
    }

    public static PlatformException buildUploadFileNotFoundException() {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildUploadFileNotFoundException();
    }

    public static PlatformException buildAuthAmbiguityException(String message) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildAuthAmbiguityException(message);
    }

    public static PlatformException buildExportException(Throwable e) {
        return EXCEPTION_FACTORY.build("export_exception", e);
    }

    public static PlatformException buildUnknownFileType(String fileName) {
        return EXCEPTION_FACTORY.build("unknown_file_type",
                Collections.singletonMap("file", fileName));
    }

    public static PlatformException buildArithmeticException(ArithmeticException e) {
        return EXCEPTION_FACTORY.build("arithmetic_exception", e);
    }

    public static PlatformException buildNotEmptyDirectoryException(String path) {
        return EXCEPTION_FACTORY.build("not_empty_directory",
                Collections.singletonMap("path", path));
    }

    public static PlatformException buildNotAbsolutePathException(String path) {
        return EXCEPTION_FACTORY.build("not_absolute_path",
                Collections.singletonMap("path", path));
    }

    public static PlatformException buildAlreadyRunningException() {
        return EXCEPTION_FACTORY.build("already_running");
    }

    public static PlatformException buildInvalidCertificateException(Throwable e) {
        return EXCEPTION_FACTORY.build(INVALID_CERTIFICATE_CODE, e);
    }

    public static PlatformException buildInvalidCertificateException(String comment) {
        return EXCEPTION_FACTORY.build(INVALID_CERTIFICATE_CODE, comment);
    }

    public static PlatformException buildReadOnlyObjectException() {
        return EXCEPTION_FACTORY.build("read_only_object");
    }

    public static PlatformException buildExcessivelyFilesException() {
        return EXCEPTION_FACTORY.build("excessive_files");
    }

    public static PlatformException buildUnexpectedBehaviourException(String comment) {
        return EXCEPTION_FACTORY.build("unexpected_behaviour", comment);
    }

    /**
     * @return Не найден обязательный параметр.
     */
    public static PlatformException buildNotFoundObligatoryParam(Class<? extends DomainObject> clazz, int fieldNumber) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotFoundObligatoryParam(clazz, fieldNumber);
    }

    public static PlatformException buildNotFoundObligatoryParam(String fieldName) {
        return com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder.buildNotFoundObligatoryParam(fieldName);
    }

    public static PlatformException buildInvalidPathException(Path path) {
        return EXCEPTION_FACTORY.build("invalid_path", path.toString());
    }

    public static PlatformException buildGeneralSecurityException(GeneralSecurityException e) {
        return EXCEPTION_FACTORY.build("general_security_exception", e);
    }

    public static PlatformException buildSchedulerException(Throwable e) {
        return EXCEPTION_FACTORY.build("scheduler_exception", e);
    }

    public static PlatformException buildIllegalApiKeyException() {
        return EXCEPTION_FACTORY.build("illegal_apikey_exception");
    }

    public static PlatformException buildFileNotExistsException() {
        return EXCEPTION_FACTORY.build("file_not_exists");
    }

    public static PlatformException buildLoadManyFileInRequestException() {
        return EXCEPTION_FACTORY.build("load_many_file_in_request_error");
    }

    public static PlatformException buildCurrentLicenseIsExpiredException(boolean isLicenseKeyInsertionAvailable) {
        return EXCEPTION_FACTORY.build("current_license_is_expired", new HashMap<>() {{
            put("is_license_key_insertion_available", isLicenseKeyInsertionAvailable);
        }});
    }

    public static PlatformException buildRemoteControllerImplementationNotFoundException(Class<?> clazz) {
        return EXCEPTION_FACTORY.build("remote_controller_implementation_not_found", new HashMap<>() {{
            put("class", clazz.getCanonicalName());
        }});
    }
}


