package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.instancemanager.ZoneManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Zone;
import com.fuzzy.subsystem.gameserver.model.entity.SevenSigns;

/**
 * format: d
 */
public class ShowMiniMap extends L2GameServerPacket
{
	private int _mapId, _period;
	private boolean canWriteImpl = true;

	public ShowMiniMap(L2Player player, int mapId)
	{
		_mapId = mapId;
		_period = SevenSigns.getInstance().getCurrentPeriod();

		L2Zone hellBoundTerrytory = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.dummy, 704004, false);
		// Map of Hellbound
		if(hellBoundTerrytory != null && player.isInZone(hellBoundTerrytory) && Functions.getItemCount(player, 9994) == 0)
		{
			player.sendPacket(Msg.THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP_THE_MINI_MAP_WILL_NOT_BE_OPENED);
			canWriteImpl = false;
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(!canWriteImpl)
			return;

		writeC(0xa3);
		writeD(_mapId);
		writeC(_period);
	}
}