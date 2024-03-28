package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

import java.util.List;

public class AutocompleteResult<Node extends DomainObject, Item extends DomainObject> {

    private List<AutocompleteElement<Node, Item>> items = null;
    private int nextCount = 0;
    private int matchCount = 0;

    public List <AutocompleteElement <Node, Item>> getItems() {
        return items;
    }

    public void setItems(List <AutocompleteElement <Node, Item>> items) {
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

