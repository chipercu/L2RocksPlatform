package com.fuzzy.subsystems.tree;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystems.list.RemoteListItem;

import java.util.ArrayList;

public class RemoteTreeElement<T extends RemoteObject> extends RemoteListItem<T> {

    private int nextCount = 0;
    private ArrayList<Long> parents = null;

    public int getNextCount() {
        return nextCount;
    }

    public void setNextCount(int nextCount) {
        this.nextCount = nextCount;
    }

    public ArrayList<Long> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Long> parents) {
        this.parents = parents;
    }
}
