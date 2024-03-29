package com.fuzzy.subsystems.tree;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystems.list.RemoteListItem;

import java.util.ArrayList;

public class RemoteTreeElement<T extends RemoteObject> extends RemoteListItem<T> {

    private boolean hasNext;
    private ArrayList<Long> parents = null;

    public boolean hasNext() {
        return hasNext;
    }

    public void setNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public ArrayList<Long> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Long> parents) {
        this.parents = parents;
    }
}
