package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public class AutocompleteElement<Node extends DomainObject, Item extends DomainObject>
        extends LightAutocompleteElement<Item> {

    private Node node = null;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Item getItem() {
        return super.getItem();
    }
}
