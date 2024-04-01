package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.config.*;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class Transformation extends L2Skill
{
	public final boolean useSummon;
	public final boolean isDisguise;
	public final String transformationName;

	public Transformation(StatsSet set)
	{
		super(set);
		useSummon = set.getBool("useSummon", false);
		isDisguise = set.getBool("isDisguise", false);
		transformationName = set.getString("transformationName", null);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		L2Player player = target.getPlayer();

		if(!player.canTransformation(useSummon, this))
			return false;
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		// В силу некоторых изменений, это должно быть здесь...
		if(!activeChar.getPlayer().canTransformation(useSummon, this))
			return;
		else if(useSummon)
		{
			if(activeChar.getPet() == null || !activeChar.getPet().isSummon() || activeChar.getPet().isDead())
			{
				activeChar.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
				return;
			}
			activeChar.getPet().unSummon();
		}

		if(isSummonerTransformation() && activeChar.getPet() != null && activeChar.getPet().isSummon())
			activeChar.getPet().unSummon();
		for(L2Character target : targets)
			if(target != null && target.isPlayer())
				getEffects(activeChar, target, false, false);

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}

	@Override
	public boolean isTransformation()
	{
		return true;
	}
}