package com.fuzzy.subsystem.gameserver.templates;

import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.skills.StatTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 19:17/19.05.2011
 */
public class OptionDataTemplate extends StatTemplate
{
	private final List<L2Skill> _skills = new ArrayList<L2Skill>(0);
	private final int _id;

	public OptionDataTemplate(int id)
	{
		_id = id;
	}

	public void addSkill(L2Skill skill)
	{
		_skills.add(skill);
	}

	public List<L2Skill> getSkills()
	{
		return _skills;
	}

	public int getId()
	{
		return _id;
	}
}
