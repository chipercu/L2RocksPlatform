package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SubjectObjectChecker<S, O> {

    void prepare(@NonNull ResourceProvider resources);

    boolean checkSubject(@NonNull S subjectId, @NonNull QueryTransaction transaction) throws PlatformException;

    boolean checkObject(@NonNull O objectId, @NonNull QueryTransaction transaction) throws PlatformException;
}
