package com.fuzzy.subsystems.collection;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.sorter.Sorter;

import java.util.ArrayList;
import java.util.function.Function;

public class RemoteCollection<Y extends DomainObject, T extends GDomainObject<Y>> implements RemoteObject {

    private ArrayList<T> items = null;
    private boolean hasNext;

    protected RemoteCollection() {
    }

    protected RemoteCollection(
            Sorter<Y> source,
            Function<Y, T> elementConstructor
    ) throws PlatformException {
        if (source.getData() != null) {
            ArrayList<T> items = new ArrayList<>();
            for (Y sourceItem : source.getData()) {
                items.add(elementConstructor.apply(sourceItem));
            }
            setItems(items);
        }
        setHasNext(source.hasNext());
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
