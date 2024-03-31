package com.fuzzy.subsystem.core.remote.fieldsgetter;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public interface RCFieldsGetter extends QueryRemoteController {

    /**
     * Возвращает описание системных полей объекта.
     * @param objectType    Тип объекта (название класса объекта, например, EmployeeReadable.class.getName()).
     * @param context       Контекст.
     * @return              Описание полей объекта.
     */
    @NonNull ArrayList<SystemFieldDescription> getSystemFields(@NonNull String objectType,
                                                               @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает описание дополнительных полей объекта.
     * @param objectType    Тип объекта (название класса объекта, например, EmployeeReadable.class.getName()).
     * @param context       Контекст.
     * @return              Описание полей объекта.
     */
    @NonNull ArrayList<AdditionalFieldDescription> getAdditionalFields(@NonNull String objectType,
                                                                       @NonNull ContextTransaction context) throws PlatformException;
}
