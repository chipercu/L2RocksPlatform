package com.fuzzy.subsystem.core.remote.additionalfieldvaluegetter;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.remote.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public interface RCAdditionalFieldValueGetter extends QueryRemoteController {

    /**
     * Возвращает значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param index             Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param context           Контекст.
     * @return Значение поля.
     */
    Optional<Long> getLongValue(long additionalFieldId,
                                long objectId,
                                int index,
                                @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param index             Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param context           Контекст.
     * @return Значение поля.
     */
    Optional<Long> getIdValue(long additionalFieldId,
                              long objectId,
                              int index,
                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает идентификаторы объектов с переданным значением в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param value             Значение поля.
     * @param context           Контекст.
     * @return Идентификаторы объектов.
     */
    HashSet<Long> findObjects(long additionalFieldId,
                              Long value,
                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает все значения поля-массива.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param context           Контекст.
     * @return Значение поля.
     */
    @NonNull ArrayList<Long> getLongArray(long additionalFieldId,
                                          long objectId,
                                          @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param index             Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param context           Контекст.
     * @return Значение поля.
     */
    Optional<String> getStringValue(long additionalFieldId,
                                    long objectId,
                                    int index,
                                    @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает идентификаторы объектов с переданным значением в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param value             Значение поля.
     * @param context           Контекст.
     * @return Идентификаторы объектов.
     */
    HashSet<Long> findObjects(long additionalFieldId,
                              String value,
                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает все значения поля-массива.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param context           Контекст.
     * @return Значение поля.
     */
    @NonNull ArrayList<String> getStringArray(long additionalFieldId,
                                              long objectId,
                                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param index             Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param context           Контекст.
     * @return Значение поля.
     */
    Optional<LocalDate> getDateValue(long additionalFieldId,
                                     long objectId,
                                     int index,
                                     @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает идентификаторы объектов с переданным значением в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param value             Значение поля.
     * @param context           Контекст.
     * @return Идентификаторы объектов.
     */
    HashSet<Long> findObjects(long additionalFieldId,
                              LocalDate value,
                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает все значения поля-массива.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param context           Контекст.
     * @return Значение поля.
     */
    @NonNull ArrayList<LocalDate> getDateArray(long additionalFieldId,
                                               long objectId,
                                               @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param index             Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param context           Контекст.
     * @return Значение поля.
     */
    Optional<Instant> getDateTimeValue(long additionalFieldId,
                                       long objectId,
                                       int index,
                                       @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает идентификаторы объектов с переданным значением в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param value             Значение поля.
     * @param context           Контекст.
     * @return Идентификаторы объектов.
     */
    HashSet<Long> findObjects(long additionalFieldId,
                              Instant value,
                              @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает все значения поля-массива.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId          ID объекта.
     * @param context           Контекст.
     * @return Значение поля.
     */
    @NonNull ArrayList<Instant> getDateTimeArray(long additionalFieldId,
                                                 long objectId,
                                                 @NonNull ContextTransaction context) throws PlatformException;
}
