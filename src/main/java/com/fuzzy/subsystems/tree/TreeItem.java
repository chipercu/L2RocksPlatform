package com.fuzzy.subsystems.tree;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.remote.Identifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TreeItem<Item extends DomainObject> extends TreeElement implements Identifiable<Long> {

    private final Item item;

    public TreeItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public @NonNull Long getIdentifier() {
        return item.getId();
    }
}
