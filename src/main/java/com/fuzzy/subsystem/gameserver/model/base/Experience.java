package com.fuzzy.subsystem.gameserver.model.base;

import com.fuzzy.subsystem.config.ConfigValue;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class Experience
{

	public final static long LEVEL[] = {
			-1L, // level 0 (unreachable)
			0L, 68L, 363L, 1168L, 2884L, 6038L, 11287L, 19423L,
			31378L,
			48229L, //level 10
			71201L, 101676L, 141192L, 191452L, 254327L, 331864L, 426284L, 539995L,
			675590L,
			835854L, //level 20
			1023775L, 1242536L, 1495531L, 1786365L, 2118860L, 2497059L, 2925229L, 3407873L,
			3949727L,
			4555766L, //level 30
			5231213L, 5981539L, 6812472L, 7729999L, 8740372L, 9850111L, 11066012L, 12395149L,
			13844879L,
			15422851L, //level 40
			17137002L, 18995573L, 21007103L, 23180442L, 25524751L, 28049509L, 30764519L, 33679907L,
			36806133L,
			40153995L, //level 50
			45524865L, 51262204L, 57383682L, 63907585L, 70852742L, 80700339L, 91162131L, 102265326L,
			114038008L,
			126509030L, //level 60
			146307211L, 167243291L, 189363788L, 212716741L, 237351413L, 271973532L, 308441375L, 346825235L,
			387197529L,
			429632402L, //level 70
			474205751L, 532692055L, 606319094L, 696376867L, 804219972L, 931275828L, 1151275834L, 1511275834L, 2044287599L,
			3075966164L, //level 80
			4295351949L, 5766985062L, 7793077345L, 10235368963L, 13180481103L, //level 85
			16890558728L, 20600636353L, 24310713978L, 28020791603L, 31730869228L, 35440946853L, 39151024478L, 42861102103L, 46571179728L, 50281257353L, 53991334978L, 57701412603L, 61411490228L, 65121567853L, 68831645478L };

	/**
	 * Return PenaltyModifier (can use in all cases)
	 *
	 * @param count	- how many times <percents> will be substructed
	 * @param percents - percents to substruct
	 *
	 * @author Styx
	 */

	/*
	 *  This is for fine view only ;)
	 *
	 *	public final static double penaltyModifier(int count, int percents)
	 *	{
	 *		int allPercents = 100;
	 *		int allSubstructedPercents = count * percents;
	 *		int penaltyInPercents = allPercents - allSubstructedPercents;
	 *		double penalty = penaltyInPercents / 100.0;
	 *		return penalty;
	 *	}
	 */
	public static double penaltyModifier(long count, double percents)
	{
		return Math.max(1. - count * percents / 100, 0);
	}

	/**
	 * Вероятно оффлайк формула
	 */
	public static double baseVitalityMod(int playerLevel, int targetLevel, double exp)
	{
		// exp / targetLevel^2 это оффовый exp_rate, для оффа модификатор затрат виталити равен 25*exp_rate/9
		// а поскольку у нас используется вдвое большая единица делим на 18
		return ConfigValue.AltVitalityConsumption * 25 * exp / (targetLevel * targetLevel * 18);
	}

	/**
	 * Максимальный достижимый уровень
	 */
	public static int getMaxLevel()
	{
		return ConfigValue.AltMaxLevel;
	}

	/**
	 * Максимальный уровень для саба
	 */
	public static int getMaxSubLevel()
	{
		return ConfigValue.AltMaxSubLevel;
	}
}