package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.instances.L2HennaInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;

public class RequestHennaUnequip extends L2GameClientPacket
{
	private int _symbolId;

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;

		for (int i = 1; i <= 3; i ++)
		{
			L2HennaInstance henna = player.getHenna(i);
			if (henna == null)
				continue;

			if (henna.getSymbolId() == _symbolId)
			{
				if(player.getInventory().getCountOf(ConfigValue.HennaRemoveItemId) >= ConfigValue.HennaRemoveItemCount)
				{
					if(ConfigValue.HennaRemoveItemCount > 0)
						player.getInventory().destroyItemByItemId(ConfigValue.HennaRemoveItemId, ConfigValue.HennaRemoveItemCount, true);
					PlayerData.getInstance().removeHenna(player, i);
					player.sendPacket(new SystemMessage(SystemMessage.THE_SYMBOL_HAS_BEEN_DELETED));
				}
				else
				{
					if(ConfigValue.HennaRemoveItemId == 57)
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					else
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				}
				
				break;
			}
		}
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		_symbolId = readD();
	}
}