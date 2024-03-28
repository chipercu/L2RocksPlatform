package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.remote.Identifiable;

public interface IdentifiableAccessSchemeItem<K, S, O, T> extends AccessSchemeItem<S, O, T>, Identifiable<K> {

}
