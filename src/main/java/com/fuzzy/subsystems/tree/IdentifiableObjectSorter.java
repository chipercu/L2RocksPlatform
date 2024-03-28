package com.fuzzy.subsystems.tree;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.remote.Identifiable;

import java.util.List;

public interface IdentifiableObjectSorter<T extends Identifiable> {

    void add(T object) throws PlatformException;

    void finish() throws PlatformException;

    void clear() throws PlatformException;

    boolean isEmpty();

    List<T> getData();

    int getNextCount();
}
