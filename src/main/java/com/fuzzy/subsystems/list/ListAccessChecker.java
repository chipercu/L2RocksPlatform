package com.fuzzy.subsystems.list;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.access.ContextAccessValidator;
import com.fuzzy.subsystems.accesscscheme.StandardAccessValidator;

public class ListAccessChecker<T extends DomainObject> implements ListChecker<T> {

    private final ContextAccessValidator<?, T> accessValidator;
    private boolean onlyEditable;

    public ListAccessChecker(StandardAccessValidator<?, T> accessValidator) {
        this.accessValidator = new ContextAccessValidator<>(accessValidator);
        this.onlyEditable = false;
    }

    public void setOnlyEditable(boolean onlyEditable) {
        this.onlyEditable = onlyEditable;
    }

    @Override
    public boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException {
        return onlyEditable ? accessValidator.checkWriteAccessByObject(item, context)
                : accessValidator.checkReadAccessByObject(item, context);
    }
}
