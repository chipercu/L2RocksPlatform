package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class VitalityUpdate extends L2Skill
{
	private final double _addPoints;
	private final double _setPoints;

	public VitalityUpdate(StatsSet set)
	{
		super(set);
		_addPoints = set.getDouble("addVitalityPoints", 0);
		_setPoints = set.getDouble("setVitalityPoints", 0);
	}

	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;
		L2Player player = activeChar.getPlayer();
		if(_addPoints != 0)
			player.setVitality(player.getVitality() + _addPoints);
		if(_setPoints >= 0)
			player.setVitality(_setPoints);
	}
}
