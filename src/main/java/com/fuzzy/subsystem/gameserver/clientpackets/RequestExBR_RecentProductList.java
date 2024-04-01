package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.itemmall.ItemMall;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestExBR_RecentProductList extends L2GameClientPacket
{
    public void readImpl() {
    }

    public void runImpl() {
        L2Player player = getClient().getActiveChar();
        if (player == null)
            return;
        ItemMall.getInstance().recentProductList(player);
    }
}