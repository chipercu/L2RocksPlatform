package com.fuzzy.subsystem.core.remote.integrations;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public interface RCIntegrations extends QueryRemoteController {

    /**
     * Возвращает список интеграций, синхронизирующих значение поля объекта.
     * @param objectType    Тип объекта (название класса объекта, например, EmployeeReadable.class.getName()).
     * @param fieldKey      Идентификатор поля.
     * @param context       Контекст.
     * @return              Список интеграций. Возможен null, если нет интеграций, синхронизирующих значение поля.
     */
    @Nullable ArrayList<String> getIntegrations(@NonNull String objectType,
                                                @NonNull String fieldKey,
                                                @NonNull ContextTransaction context) throws PlatformException;

    /**
     * Возвращает список интеграций, синхронизирующих значение поля объекта.
     * @param objectId      Уникальный идентификатор объекта.
     * @param objectType    Тип объекта (название класса объекта, например, EmployeeReadable.class.getName()).
     * @param fieldKey      Идентификатор поля.
     * @param context       Контекст.
     * @return              Список интеграций. Возможен null, если нет интеграций, синхронизирующих значение поля.
     */
    @Nullable
    ArrayList<String> getIntegrations(long objectId,
                                      @NonNull String objectType,
                                      @NonNull String fieldKey,
                                      @NonNull ContextTransaction context) throws PlatformException;
}
