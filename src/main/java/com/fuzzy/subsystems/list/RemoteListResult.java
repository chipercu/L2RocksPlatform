package com.fuzzy.subsystems.list;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.function.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RemoteListResult<
        E extends DomainObject,
        Y extends RemoteObject,
        T extends RemoteListItem<? super Y> & RemoteObject> implements RemoteObject {

    private ArrayList<T> items = null;
    private int matchCount = 0;
    private boolean hasNext;

    protected RemoteListResult() {
    }

    protected RemoteListResult(ArrayList<T> items, int matchCount, boolean hasNext) {
        this.items = items;
        this.matchCount = matchCount;
        this.hasNext = hasNext;
    }

    protected RemoteListResult(
            ListResult<E> source,
            Function<E, Y> elementConstructor,
            Class<T> itemClazz
    ) throws PlatformException {
        if (source.items() != null) {
            ArrayList<T> items = new ArrayList<>();
            for (ListItem<E> sourceItem : source.items()) {
                T item;
                try {
                    Constructor<T> constructor = itemClazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    item = constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException |
                        InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                item.setElement(elementConstructor.apply(sourceItem.item()));
                item.setSelected(sourceItem.selected());
                item.setHidden(sourceItem.hidden());
                items.add(item);
            }
            setItems(items);
        }
        setNext(source.hasNext());
        setMatchCount(source.matchCount());
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}