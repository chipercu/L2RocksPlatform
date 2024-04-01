package com.fuzzy.subsystem.gameserver.model.base;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.serverpackets.SkillList;
import com.fuzzy.subsystem.gameserver.tables.SkillTable;
import com.fuzzy.subsystem.gameserver.templates.OptionDataTemplate;
import com.fuzzy.subsystem.gameserver.xml.loader.XmlOptionDataLoader;

public final class L2Augmentation
{
	private int _effectsId = 0;
	private L2Skill _skill = null;
	private boolean _isLoaded = false;

	public boolean isLoaded()
	{
		return _isLoaded;
	}

	public L2Augmentation(int effects, L2Skill skill)
	{
		_effectsId = effects;
		_isLoaded = true;
		_skill = skill;
	}

	public L2Augmentation(int effects, int skill, int skillLevel)
	{
		this(effects, SkillTable.getInstance().getInfo(skill, skillLevel));
	}

	/**
	 * Get the augmentation "id" used in serverpackets.
	 * @return augmentationId
	 */
	public int getAugmentationId()
	{
		return _effectsId;
	}

	public L2Skill getSkill()
	{
		return _skill;
	}

	/**
	 * Applys the boni to the player.
	 * @param player
	 */
	public void applyBoni(L2Player player, boolean sendSkillList)
	{
		// При несоотвествии грейда аугмент не применяется
		if(player.getWeaponsExpertisePenalty() > 0 || player.getArmorExpertisePenalty() > 0)
			return;

		int stats[] = new int[2];
		stats[0] = 0x0000FFFF & getAugmentationId();
		stats[1] = getAugmentationId() >> 16;

		boolean sendList = false;
		boolean sendReuseList = false;
		for(int i : stats)
		{
			//System.out.println("stats("+getAugmentationId()+"): "+i);
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(i);
			if(template == null)
				continue;

			player.addStatFuncs(template.getStatFuncs(template));

			for(L2Skill skill : template.getSkills())
			{
				sendList = true;
				player.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()));
				if(player.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId()))
					sendReuseList = true;
			}

			player.addTriggers(template);
		}

		if(sendSkillList && sendList)
			player.sendPacket(new SkillList(player));

		player.updateStats();
	}

	/**
	 * Removes the augmentation boni from the player.
	 * @param player
	 */
	public void removeBoni(L2Player player, boolean sendSkillList)
	{
		int stats[] = new int[2];
		stats[0] = 0x0000FFFF & getAugmentationId();
		stats[1] = getAugmentationId() >> 16;

		boolean sendList = false;
		for(int i : stats)
		{
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(i);
			if(template == null)
				continue;

			player.removeStatsOwner(template);

			for(L2Skill skill : template.getSkills())
			{
				sendList = true;
				player.removeSkill(skill, false, false);
			}

			player.removeTriggers(template);
		}

		if(sendSkillList && sendList)
			player.sendPacket(new SkillList(player));

		player.updateEffectIcons();
		player.updateStats();
	}

	@Override
	public String toString()
	{
		return "Augmentation";
	}
}