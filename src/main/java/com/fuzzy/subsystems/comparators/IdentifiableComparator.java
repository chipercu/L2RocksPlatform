package com.fuzzy.subsystems.comparators;

import com.fuzzy.subsystems.remote.Identifiable;

public class IdentifiableComparator<K extends Comparable<K>, T extends Identifiable<K>> extends IdComparator<K, T> {

    public IdentifiableComparator() {
        super(Identifiable::getIdentifier);
    }
}
