package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.MagicSkillLaunched;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.tables.NpcTable;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Location;

import java.util.logging.Logger;

public final class EffectSymbol extends L2Effect
{
	private static final Logger log = Logger.getLogger(EffectSymbol.class.getName());

	L2NpcInstance _symbol;

	public EffectSymbol(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getSkill().getTargetType() != L2Skill.SkillTargetType.TARGET_SELF)
		{
			log.severe("Symbol skill with target != self, id = " + getSkill().getId());
			return false;
		}

		L2Skill skill = getSkill().getFirstAddedSkill();
		if(skill == null)
		{
			log.severe("Not implemented symbol skill, id = " + getSkill().getId());
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		L2Skill skill = getSkill().getFirstAddedSkill();

		// Затычка, в клиенте они почему-то не совпадают.
		skill.setIsMagic(getSkill().getMagic());

		Location loc = _effected.getLoc();
		if(_effected.isPlayer() && ((L2Player) _effected).getGroundSkillLoc() != null)
		{
			loc = ((L2Player) _effected).getGroundSkillLoc();
			((L2Player) _effected).setGroundSkillLoc(null);
		}

		L2NpcTemplate template = NpcTable.getTemplate(_skill.getSymbolId());

		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(loc);
			spawn.setReflection(_effected.getReflection().getId());
			spawn.setAmount(1);
			spawn.init();
			spawn.stopRespawn();
			_symbol = spawn.getLastSpawn();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_symbol == null)
			return;
		_symbol.deleteMe();
		_symbol = null;
	}

	@Override
	public boolean onActionTime()
	{
		if(_template._counter <= 1)
			return false;

		L2Character effector = getEffector();
		L2Skill skill = getSkill().getFirstAddedSkill();
		L2NpcInstance symbol = _symbol;
		double mpConsume = skill.getMpConsume();

		if(effector == null || skill == null || symbol == null)
			return false;

		if(mpConsume > effector.getCurrentMp())
		{
			effector.sendPacket(Msg.NOT_ENOUGH_MP);
			return false;
		}

		effector.reduceCurrentMp(mpConsume, effector);

		// Использовать разрешено только скиллы типа TARGET_ONE
		for(L2Character cha : L2World.getAroundCharacters(symbol, skill.getAffectRange(), 200))
			if(cha != null/* && cha.getEffectList().getEffectsBySkill(skill) == null*/ && skill.checkTarget(effector, cha, cha, false, false) == null)
			{
				if(skill.isOffensive() && !GeoEngine.canAttacTarget(symbol, cha, false))
					continue;
				if(effector == null || cha == null || symbol == null || getSkill() == null)
					continue;
				GArray<L2Character> targets = new GArray<L2Character>(1);
				targets.add(cha);
				effector.callSkill(skill, targets, false);
				//skill.useSkill(symbol, targets);
				//symbol.callSkill(skill, targets, false);
				if(effector.checkPvP(cha, skill))
					effector.startPvPFlag(cha);
				try
				{
					effector.broadcastSkill(new MagicSkillLaunched(symbol.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), cha, true), true);
				}
				catch(Exception e) // тут иногда вылазит НПЕ, лень искать почему...
				{
					return false;
					//_log.info("EffectSymbol: onActionTime -->: symbol="+symbol+" skill="+skill+" effector="+effector);
					//e.printStackTrace();
				}
				if(skill.isOffensive())
					cha.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, new Object[] { effector, 1, skill });
			}
		return true;
	}
}