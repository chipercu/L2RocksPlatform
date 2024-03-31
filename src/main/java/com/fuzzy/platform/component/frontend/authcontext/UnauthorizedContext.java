package com.fuzzy.platform.component.frontend.authcontext;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

/**
 * Created by kris on 08.02.17.
 */
public class UnauthorizedContext implements RemoteObject {

    public static final String TO_STRING = "UnauthorizedContext";

    @Override
    public String toString() {
        return TO_STRING;
    }
}
