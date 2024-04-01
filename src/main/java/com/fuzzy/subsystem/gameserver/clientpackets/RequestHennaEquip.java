package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.instances.L2HennaInstance;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.model.items.PcInventory;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.tables.HennaTable;
import com.fuzzy.subsystem.gameserver.tables.HennaTreeTable;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;
import com.fuzzy.subsystem.gameserver.templates.L2Henna;

public class RequestHennaEquip extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * packet type id 0x6F
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		_symbolId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Henna template = HennaTable.getInstance().getTemplate(_symbolId);
		if(template == null)
			return;

		L2HennaInstance temp = new L2HennaInstance(template);

		boolean cheater = true;
		for(L2HennaInstance h : HennaTreeTable.getInstance().getAvailableHenna(activeChar.getClassId(), activeChar.getSex()))
			if(h.getSymbolId() == temp.getSymbolId())
			{
				cheater = false;
				break;
			}

		if(cheater)
		{
			activeChar.sendPacket(Msg.THE_SYMBOL_CANNOT_BE_DRAWN);
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance item = inventory.getItemByItemId(temp.getItemIdDye());
		if(item != null && item.getCount() >= temp.getAmountDyeRequire() && activeChar.getInventory().getCountOf(ConfigValue.HennaItemId) >= temp.getPrice() && PlayerData.getInstance().addHenna(activeChar, temp))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED).addString(temp.getName()), Msg.THE_SYMBOL_HAS_BEEN_ADDED);
			if(temp.getPrice() > 0)
				inventory.destroyItemByItemId(ConfigValue.HennaItemId, temp.getPrice(), true);
			if(inventory.destroyItemByItemId(temp.getItemIdDye(), temp.getAmountDyeRequire(), true) == null)
				_log.info("RequestHennaEquip[50]: Item not found!!!");
		}
		else
			activeChar.sendPacket(Msg.THE_SYMBOL_CANNOT_BE_DRAWN);
	}
}