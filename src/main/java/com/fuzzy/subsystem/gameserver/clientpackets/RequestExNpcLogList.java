package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.QuestManager;
import com.fuzzy.subsystem.gameserver.model.quest.QuestState;
import com.fuzzy.subsystem.gameserver.serverpackets.ExQuestNpcLogList;

/**
 * Format: d
 * @author Drizzy
 */

public class RequestExNpcLogList extends L2GameClientPacket
{
	private int _questId;

	@Override
	protected void readImpl()
	{
		_questId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		QuestState st = getClient().getActiveChar().getQuestState(QuestManager.getQuest(_questId).getName());
		getClient().sendPacket(new ExQuestNpcLogList(st));
	}
}