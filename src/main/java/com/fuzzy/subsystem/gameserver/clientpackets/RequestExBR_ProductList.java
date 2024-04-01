package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.itemmall.ItemMall;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestExBR_ProductList extends L2GameClientPacket
{
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        L2Player player = getClient().getActiveChar();

        if (player == null)
            return;
        else
            ItemMall.getInstance().showList(player);
    }
}