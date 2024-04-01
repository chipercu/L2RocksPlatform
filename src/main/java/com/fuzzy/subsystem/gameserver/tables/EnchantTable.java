package com.fuzzy.subsystem.gameserver.tables;

import javolution.util.FastMap;
import com.fuzzy.subsystem.gameserver.model.base.L2EnchantSkillLearn;
import com.fuzzy.subsystem.util.GArray;

public abstract class EnchantTable
{
	public static FastMap<Integer, GArray<L2EnchantSkillLearn>> _enchant = new FastMap<Integer, GArray<L2EnchantSkillLearn>>().setShared(true);
}