package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class GiveVitality extends L2Skill
{
	private int give = 0;
	
    public GiveVitality(StatsSet set)
	{
        super(set);
        give = set.getInteger("give", 0);
    }
	
    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for (L2Character target : targets)
			if(target != null && target.isPlayer())
			{
				if(hasEffects())
				{
					target.getPlayer().getEffectList().getEffectBySkillId(getId()).exit(true, false);
					getEffects(activeChar, target.getPlayer(), false , false);
					SystemMessage sm = new SystemMessage(SystemMessage.S1_S2S_EFFECT_CAN_BE_FELT);
					sm.addSkillName(_id, _level);
					target.sendPacket(sm);
				}
				if(give > 0)
				{
					if(activeChar.getPlayer() != null)
					{
						activeChar.getPlayer().setVitality(activeChar.getPlayer().getVitality() + give);
						activeChar.getPlayer().sendPacket(Msg.YOU_HAVE_GAINED_VITALITY_POINTS);
					}
				}
			}
	}
}