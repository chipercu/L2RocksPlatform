package com.fuzzy.subsystems.struct;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteElement;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.utils.DomainObjectListGetter;

import java.util.ArrayList;
import java.util.List;

public class RemoteRDomainObjectCollectionResult<T extends DomainObject, Y>
        extends RemoteCollectionResult<Y> {

    public RemoteRDomainObjectCollectionResult() {
    }

    public RemoteRDomainObjectCollectionResult(
            Function<T, Y> constructor,
            DomainObjectListGetter<T>.Result source
    ) throws PlatformException {
        this.setItems(convert(constructor, source.items));
        this.setNextCount(source.nextCount);
        this.setMatchCount(source.items.size() + source.nextCount);
    }

    public RemoteRDomainObjectCollectionResult(
            Function<T, Y> constructor,
            LightAutocompleteResult<T> source
    ) throws PlatformException {
        ArrayList<Y> items = new ArrayList<>();
        for (LightAutocompleteElement<T> sourceItem : source.getItems()) {
            items.add(constructor.apply(sourceItem.getItem()));
        }
        setItems(items);
        setNextCount(source.getNextCount());
        setMatchCount(source.getMatchCount());
    }

    private static <T extends DomainObject, Y> ArrayList<Y> convert(Function<T, Y> constructor, List<T> source)
            throws PlatformException {
        ArrayList<Y> result = new ArrayList <>();
        for (T sourceItem : source) {
            result.add(constructor.apply(sourceItem));
        }
        return result;
    }
}
