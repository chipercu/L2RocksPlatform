package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SubjectMergerHelper<S, O, T, K extends AccessSchemeItem<S, O, T>> {

    @FunctionalInterface
    public interface ForEachBySubjectFunction<S, K> {
        void apply(@NonNull S subjectId,
                   @NonNull Function<K, Boolean> handler,
                   @NonNull QueryTransaction transaction) throws PlatformException;
    }

    @FunctionalInterface
    public interface AddingAccessFunction<S, O, T> {
        void apply(@NonNull S subjectId,
                   @NonNull O objectId,
                   @NonNull T operation,
                   @NonNull ContextTransaction<?> context) throws PlatformException;
    }

    @FunctionalInterface
    public interface RemovingSubjectFunction<S> {
        void apply(@NonNull S subjectId,
                   @NonNull ContextTransaction<?> context) throws PlatformException;
    }

    private final ForEachBySubjectFunction<S, K> forEachBySubjectFunction;
    private final AddingAccessFunction<S, O, T> addingAccessFunction;
    private final RemovingSubjectFunction<S> removingSubjectFunction;

    public SubjectMergerHelper(@NonNull ForEachBySubjectFunction<S, K> forEachBySubjectFunction,
                               @NonNull AddingAccessFunction<S, O, T> addingAccessFunction,
                               @NonNull RemovingSubjectFunction<S> removingSubjectFunction) {
        this.forEachBySubjectFunction = forEachBySubjectFunction;
        this.addingAccessFunction = addingAccessFunction;
        this.removingSubjectFunction = removingSubjectFunction;
    }

    public void merge(@NonNull S mainSubjectId,
                      @NonNull HashSet<S> secondarySubjects,
                      @NonNull ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        List<ObjectAccess<O, T>> objects = new ArrayList<>();
        for (S secondarySubjectId : secondarySubjects) {
            if (secondarySubjectId != null) {
                forEachBySubjectFunction.apply(secondarySubjectId, accessSchemeItem -> {
                    objects.add(new ObjectAccess<>(accessSchemeItem.getObjectId(), accessSchemeItem.getOperation()));
                    return true;
                }, transaction);
                removingSubjectFunction.apply(secondarySubjectId, context);
            }
        }
        for (ObjectAccess<O, T> objectAccess : objects) {
            addingAccessFunction.apply(mainSubjectId, objectAccess.object(), objectAccess.operation(), context);
        }
    }

    private record ObjectAccess<O, T>(@NonNull O object, @NonNull T operation) {}
}
