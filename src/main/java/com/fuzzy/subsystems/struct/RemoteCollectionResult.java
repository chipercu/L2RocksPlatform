package com.fuzzy.subsystems.struct;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.util.ArrayList;

public abstract class RemoteCollectionResult<T> implements RemoteObject {

    private ArrayList<T> items = null;
    private int nextCount = 0;
    private int matchCount = 0;

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

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
}
