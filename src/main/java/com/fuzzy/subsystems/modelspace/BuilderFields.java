package com.fuzzy.subsystems.modelspace;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by kris on 24.01.17.
 */
public class BuilderFields implements RemoteObject {

    protected final HashMap<Integer, Serializable> fields = new HashMap<>();

    public BuilderFields with(int fieldNumber, Serializable value) {
        fields.put(fieldNumber, value);
        return this;
    }

    public boolean isContain(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    public Serializable get(int fieldNumber) {
        return fields.get(fieldNumber);
    }
}
