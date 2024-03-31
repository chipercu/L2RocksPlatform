package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.database.domainobject.DomainObject;

import java.util.List;

public class AutocompleteResult<Node extends DomainObject, Item extends DomainObject> {

    private List<AutocompleteElement<Node, Item>> items = null;
    private boolean hasNext;
    private int matchCount = 0;

    public List <AutocompleteElement <Node, Item>> getItems() {
        return items;
    }

    public void setItems(List <AutocompleteElement <Node, Item>> items) {
        this.items = items;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
}

