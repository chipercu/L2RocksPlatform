package com.fuzzy.subsystem.gameserver.clientpackets;

import javolution.util.FastMap;
import com.fuzzy.subsystem.gameserver.instancemanager.RaidBossSpawnManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExGetBossRecord;
import com.fuzzy.subsystem.gameserver.serverpackets.ExGetBossRecord.BossRecordInfo;
import com.fuzzy.subsystem.util.GArray;

import java.util.Map.Entry;

/**
 * Format: (ch) d
 */
public class RequestGetBossRecord extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _bossID;

	@Override
	public void readImpl()
	{
		_bossID = readD(); // always 0?
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		int totalPoints = 0;
		int ranking = 0;

		if(activeChar == null)
			return;

		GArray<BossRecordInfo> list = new GArray<BossRecordInfo>();
		FastMap<Integer, Integer> points = RaidBossSpawnManager.getInstance().getPointsForOwnerId(activeChar.getObjectId());
		if(points != null && !points.isEmpty())
			for(Entry<Integer, Integer> e : points.entrySet())
				switch(e.getKey())
				{
					case -1: // RaidBossSpawnManager.KEY_RANK
						ranking = e.getValue();
						break;
					case 0: //  RaidBossSpawnManager.KEY_TOTAL_POINTS
						totalPoints = e.getValue();
						break;
					default:
						list.add(new BossRecordInfo(e.getKey(), e.getValue(), 0));
				}

		activeChar.sendPacket(new ExGetBossRecord(ranking, totalPoints, list));
	}
}