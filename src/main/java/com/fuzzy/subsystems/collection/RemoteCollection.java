package com.fuzzy.subsystems.collection;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.sorter.Sorter;

import java.util.ArrayList;
import java.util.function.Function;

public class RemoteCollection<Y extends DomainObject, T extends GDomainObject<Y>> implements RemoteObject {

    private ArrayList<T> items = null;
    private int nextCount = 0;

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
        setNextCount(source.getNextCount());
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public int getNextCount() {
        return nextCount;
    }

    public void setNextCount(int nextCount) {
        this.nextCount = nextCount;
    }
}
