package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.access.ContextAccessValidator;
import com.fuzzy.subsystems.accesscscheme.StandardAccessValidator;
import com.fuzzy.subsystems.textfilter.TextFilterGetter;

import java.util.Collection;

public class AtomicAutocompleteWithAccessValidation<T extends DomainObject> extends AtomicAutocompleteImpl<T> {

    private final ContextAccessValidator<?, T> accessValidator;
    private boolean onlyEditable;

    public AtomicAutocompleteWithAccessValidation(TextFilterGetter<T> textFilterGetter,
                                                  Collection<Integer> displayNameFieldNumbers,
                                                  PathGetter<T> pathGetter,
                                                  StandardAccessValidator<?, T> accessValidator) {
        super(textFilterGetter, displayNameFieldNumbers, pathGetter);
        this.accessValidator = new ContextAccessValidator<>(accessValidator);
        this.onlyEditable = false;
    }

    public void setOnlyEditable(boolean onlyEditable) {
        this.onlyEditable = onlyEditable;
    }

    @Override
    protected boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException {
        return super.checkItem(item, context)
                && (onlyEditable ? accessValidator.checkWriteAccessByObject(item, context)
                : accessValidator.checkReadAccessByObject(item, context));
    }
}
