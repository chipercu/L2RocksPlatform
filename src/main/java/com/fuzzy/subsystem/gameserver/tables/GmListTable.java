package com.fuzzy.subsystem.gameserver.tables;

import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;
import com.fuzzy.subsystem.util.GArray;

public class GmListTable
{
	public static GArray<L2Player> getAllGMs()
	{
		GArray<L2Player> gmList = new GArray<L2Player>();
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player.isGM())
				gmList.add(player);

		return gmList;
	}

	public static GArray<L2Player> getAllVisibleGMs()
	{
		GArray<L2Player> gmList = new GArray<L2Player>();
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player.isGM() && !player.isInvisible())
				gmList.add(player);

		return gmList;
	}

	public static void sendListToPlayer(L2Player player)
	{
		/*GArray<L2Player> gmList = getAllVisibleGMs();
		if(gmList.isEmpty())
		{
			player.sendPacket(Msg.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY);
			return;
		}

		player.sendPacket(Msg._GM_LIST_);
		for(L2Player gm : gmList)
			player.sendPacket(new SystemMessage(SystemMessage.GM_S1).addString(gm.getName()));*/
	}

	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for(L2Player gm : getAllGMs())
			gm.sendPacket(packet);
	}

	public static void broadcastMessageToGMs(String message)
	{
		for(L2Player gm : getAllGMs())
			gm.sendMessage(message);
	}
}