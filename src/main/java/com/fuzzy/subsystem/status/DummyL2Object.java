package com.fuzzy.subsystem.status;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Object;

public class DummyL2Object extends L2Object {
    public DummyL2Object() {
        super(-1);
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return false;
    }
}