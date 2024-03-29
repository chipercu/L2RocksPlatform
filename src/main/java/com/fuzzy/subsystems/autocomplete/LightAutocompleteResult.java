package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

import java.util.List;

public class LightAutocompleteResult<Item extends DomainObject> {

    private List<LightAutocompleteElement<Item>> items = null;
    private boolean hasNext;
    private int matchCount = 0;

    public List <LightAutocompleteElement <Item>> getItems() {
        return items;
    }

    public void setItems(List <LightAutocompleteElement <Item>> items) {
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
