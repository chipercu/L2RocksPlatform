package com.fuzzy.subsystems.remote;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.function.Consumer;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

public class MultiExecutor<T> {

    private final Collection<T> controllers;

    public MultiExecutor(@NonNull Collection<T> controllers) {
        this.controllers = controllers;
    }

    public void exec(@NonNull Consumer<T> consumer) throws PlatformException {
        for (T controller : controllers) {
            consumer.accept(controller);
        }
    }

    public <U, K extends Collection<U>> K apply(@NonNull Function<T, K> function,
                                                @NonNull Supplier<K> constructor) throws PlatformException {
        K result = constructor.get();
        for (T controller : controllers) {
            K items = function.apply(controller);
            if (items != null) {
                result.addAll(items);
            }
        }
        return result;
    }

    public boolean isExistTrueResult(@NonNull Function<T, Boolean> consumer) throws PlatformException {
        for (T controller : controllers) {
            if (consumer.apply(controller)) {
                return true;
            }
        }
        return false;
    }

    public @Nullable <K> K getFirstNotNull(@NonNull Function<T, K> function) throws PlatformException {
        for (T controller : controllers) {
            K result = function.apply(controller);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}