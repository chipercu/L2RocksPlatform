package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public interface ExternalAccessSchemeItem<K, S, O>
        extends RemoteObject, IdentifiableAccessSchemeItem<K, S, O, GAccessSchemeOperation> {

}
