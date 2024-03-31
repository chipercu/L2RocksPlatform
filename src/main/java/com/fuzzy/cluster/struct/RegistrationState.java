package com.fuzzy.cluster.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

/**
 * Created by kris on 29.12.16.
 */
public class RegistrationState implements RemoteObject {

    public final int id;

    public RegistrationState(int id) {
        this.id = id;
    }
}
