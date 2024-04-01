package com.fuzzy.subsystem.util;

import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.tables.FakePlayersTable;

public class Stats {
    public static int getOnline() {
        return L2ObjectsStorage.getAllPlayersCount();
    }

    public static int getOnline(boolean includeFake) {
        return getOnline() + (includeFake ? FakePlayersTable.getFakePlayersCount() : 0);
    }
}