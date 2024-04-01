package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.quest.QuestNpcLogInfo;
import com.fuzzy.subsystem.gameserver.model.quest.QuestState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Format: ch(dddd)
 * @author Drizzy
 */

public class ExQuestNpcLogList extends L2GameServerPacket
{
	private int _questId;
	private List<int[]> _logList = Collections.emptyList();

	public ExQuestNpcLogList(QuestState state)
	{
		_questId = state.getQuest().getQuestIntId();
		int cond = state.getCond();
		List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
		if(vars == null)
			return;

		_logList = new ArrayList<int[]>(vars.size());
		for(QuestNpcLogInfo entry : vars)
		{
			int[] i = new int[2];
			i[0] = entry.getNpcIds()[0] + 1000000;
			i[1] = state.getInt(entry.getVarName());
			_logList.add(i);
		}
	}

	@Override
	protected void writeImpl()
	{
        writeC(0xFE);
        writeHG(0xC5);		
		writeD(_questId);
		writeC(_logList.size());
		for(int i = 0; i < _logList.size(); i++)
		{
			int[] values = _logList.get(i);
			writeD(values[0]);
			writeC(0);      // isNpcString
			writeD(values[1]);
		}
	}
}