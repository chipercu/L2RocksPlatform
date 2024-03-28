package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public class LightAutocompleteElement<Item extends DomainObject> {

    private Item item = null;
    private long weight = 0;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
