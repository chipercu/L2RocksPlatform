package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.instancemanager.HellboundManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.taskmanager.DecayTaskManager;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public final class NativePrisonerInstance extends L2NpcInstance
{
	public NativePrisonerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void onSpawn()
	{
		//startAbnormalEffect(2048);
		super.onSpawn();
	}

	public void onBypassFeedback(L2Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		if (st.nextToken().equals("rescue"))
		{
			//stopAbnormalEffect(2048);
			Functions.npcSay(this, "Thank you for saving me! Guards are coming, run!");
			HellboundManager.getInstance().addPoints(20);
			DecayTaskManager.getInstance().addDecayTask(this);
			setBusy(true);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}