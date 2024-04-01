package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExStorageMaxCount;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;

/**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public class StorageSizeListener extends StatsChangeListener {

    public StorageSizeListener(Stats stat) {
        super(stat);
    }

    @Override
    public void statChanged(Double oldValue, double newValue, double baseValue, Env env) {
        _calculator._character.sendPacket(new ExStorageMaxCount((L2Player) _calculator._character));
    }
}