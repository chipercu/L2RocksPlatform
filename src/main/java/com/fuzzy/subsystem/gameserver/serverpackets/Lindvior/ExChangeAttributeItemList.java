package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

//import lineage2.gameserver.model.items.ItemInfo;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExChangeAttributeItemList extends L2GameServerPacket {
    //private final ItemInfo[] _itemsList;
    private final int _itemId;

    public ExChangeAttributeItemList(int itemId/*, ItemInfo[] itemsList*/) {
        _itemId = itemId;
        //_itemsList = itemsList;
    }

    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0x118);
        writeD(_itemId);
        //writeD(_itemsList.length); // size
        //for (ItemInfo item : _itemsList)
        //	writeItemInfo(item);
    }
}
