package com.fuzzy.subsystems.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
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
