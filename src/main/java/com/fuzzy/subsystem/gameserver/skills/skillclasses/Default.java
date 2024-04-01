package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class Default extends L2Skill
{
	public Default(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		//activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Default.NotImplemented", activeChar).addNumber(getId()).addString("" + getSkillType()));
		activeChar.sendActionFailed();
	}
}
