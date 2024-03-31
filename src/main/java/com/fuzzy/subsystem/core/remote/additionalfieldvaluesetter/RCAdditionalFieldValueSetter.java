package com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.time.LocalDate;

public interface RCAdditionalFieldValueSetter extends QueryRemoteController {

    /**
     * Добавляет значение в поле. Для полей с типом немассив происходит не добавление, а замена значения.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param value    Значение поля.
     * @param context  Контекст.
     * @return Индекс добавленного значения.
     */
    int addLongValue(long additionalFieldId,
                     long objectId,
                     Long value,
                     @NonNull ContextTransaction context) throws PlatformException;


    /**
     * Добавляет значение в поле. Для полей с типом немассив происходит не добавление, а замена значения.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param value    Значение поля.
     * @param context  Контекст.
     * @return Индекс добавленного значения.
     */
    int addStringValue(long additionalFieldId,
                       long objectId,
                       String value,
                       @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Добавляет значение в поле. Для полей с типом немассив происходит не добавление, а замена значения.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param value    Значение поля.
     * @param context  Контекст.
     * @return Индекс добавленного значения.
     */
    int addDateValue(long additionalFieldId,
                     long objectId,
                     LocalDate value,
                     @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Добавляет значение в поле. Для полей с типом немассив происходит не добавление, а замена значения.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param value    Значение поля.
     * @param context  Контекст.
     * @return Индекс добавленного значения.
     */
    int addDateTimeValue(long additionalFieldId,
                         long objectId,
                         Instant value,
                         @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Обновляет значение в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index    Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param value    Новое значение поля.
     * @param context  Контекст.
     */
    void setLongValue(long additionalFieldId,
                      long objectId,
                      int index,
                      Long value,
                      @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Обновляет значение в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index    Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param value    Новое значение поля.
     * @param context  Контекст.
     */
    void setIdValue(long additionalFieldId,
                      long objectId,
                      int index,
                      Long value,
                      @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Обновляет значение в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index    Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param value    Новое значение поля.
     * @param context  Контекст.
     */
    void setStringValue(long additionalFieldId,
                        long objectId,
                        int index,
                        String value,
                        @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Обновляет значение в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index    Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param value    Новое значение поля.
     * @param context  Контекст.
     */
    void setDateValue(long additionalFieldId,
                      long objectId,
                      int index,
                      LocalDate value,
                      @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Обновляет значение в поле.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index    Индекс значения в поле (для полей с типом немассив индекс должен быть 0).
     * @param value    Новое значение поля.
     * @param context  Контекст.
     */
    void setDateTimeValue(long additionalFieldId,
                          long objectId,
                          int index,
                          Instant value,
                          @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Удаляет элемент массива для поля. Для полей с типом немассив генерирует исключение.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param index Индекс элемента массива.
     * @param context  Контекст.
     */
    void removeValue(long additionalFieldId,
                     long objectId,
                     int index,
                     @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Удаляет значение поля.
     *
     * @param additionalFieldId Идентификатор поля.
     * @param objectId ID объекта.
     * @param context  Контекст.
     */
    void removeValue(long additionalFieldId,
                     long objectId,
                     @NonNull ContextTransaction context) throws PlatformException;
}
