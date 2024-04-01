package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.config.*;
import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Manor;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.instances.L2MonsterInstance;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

public class Sowing extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		// Проверки в хэндлере
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public Sowing(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		L2Player player = (L2Player) activeChar;
		int seed_id = player.getUseSeed();
		// remove seed from inventory
		L2ItemInstance seedItem = player.getInventory().getItemByItemId(seed_id);
		if(seedItem != null)
		{
			player.sendPacket(SystemMessage.removeItems(seed_id, 1));
			player.getInventory().destroyItem(seedItem, 1, true);
		}
		else
		{
			activeChar.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		for(L2Character target : targets)
			if(target != null)
			{
				L2MonsterInstance monster = (L2MonsterInstance) target;
				if(monster.isSeeded())
					continue;

				// обработка
				double SuccessRate = (L2Manor.getInstance().isAlternative(seed_id) ? ConfigValue.BasePercentChanceOfSowingAltSuccess : ConfigValue.BasePercentChanceOfSowingSuccess);

				double diffPlayerTarget = Math.abs(activeChar.getLevel() - target.getLevel());
				double diffSeedTarget = Math.abs(L2Manor.getInstance().getSeedLevel(seed_id) - target.getLevel());

				// Штраф, на разницу уровней между мобом и игроком
				// 5% на каждый уровень при разнице >5 - по умолчанию
				if(diffPlayerTarget > ConfigValue.MinDiffPlayerMob)
					SuccessRate -= (diffPlayerTarget - ConfigValue.MinDiffPlayerMob) * ConfigValue.DiffPlayerMobPenalty;

				// Штраф, на разницу уровней между семечкой и мобом
				// 5% на каждый уровень при разнице >5 - по умолчанию
				if(diffSeedTarget > ConfigValue.MinDiffSeedMob)
					SuccessRate -= (diffSeedTarget - ConfigValue.MinDiffSeedMob) * ConfigValue.DiffSeedMobPenalty;

				// Минимальный шанс успеха всегда 1%
				if(SuccessRate < 1)
					SuccessRate = 1;

				if(ConfigValue.SkillsShowChance && !player.getVarB("SkillsHideChance"))
					activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Sowing.Chance", activeChar).addNumber((long) SuccessRate));

				if(Rnd.chance(SuccessRate))
				{
					monster.setSeeded(seedItem.getItem(), player);
					activeChar.sendPacket(Msg.THE_SEED_WAS_SUCCESSFULLY_SOWN);
				}
				else
					activeChar.sendPacket(Msg.THE_SEED_WAS_NOT_SOWN);
			}
	}
}