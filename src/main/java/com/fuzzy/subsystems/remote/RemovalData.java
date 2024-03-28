package com.fuzzy.subsystems.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class RemovalData implements Serializable {

    private final HashSet<Long> removed = new HashSet<>();
    private final HashSet<Long> nonRemoved = new HashSet<>();
    private final ArrayList<CauseForNonRemove> causesForNonRemove = new ArrayList<>();

    public HashSet<Long> getRemoved() {
        return removed;
    }

    public HashSet<Long> getNonRemoved() {
        return nonRemoved;
    }

    public ArrayList<CauseForNonRemove> getCausesForNonRemove() {
        return causesForNonRemove;
    }

    public void addNonRemoved(String cause, Collection<Long> nonRemoved) {
        if (!nonRemoved.isEmpty()) {
            this.nonRemoved.addAll(nonRemoved);
            ensureCause(cause).getNonRemoved().addAll(nonRemoved);
        }
    }

    private CauseForNonRemove ensureCause(String cause) {
        for (CauseForNonRemove causeObject : causesForNonRemove) {
            if (Objects.equals(causeObject.getCause(), cause)) {
                return causeObject;
            }
        }
        causesForNonRemove.add(new CauseForNonRemove(cause, new HashSet<>()));
        return causesForNonRemove.get(causesForNonRemove.size() - 1);
    }
}
