package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.instances.L2ChestInstance;
import com.fuzzy.subsystem.gameserver.model.instances.L2DoorInstance;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

public class Unlock extends L2Skill
{
	final int _unlockPower;

	public Unlock(StatsSet set)
	{
		super(set);
		_unlockPower = set.getInteger("unlockPower", 0) + 100;
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || target instanceof L2ChestInstance && target.isDead())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(target instanceof L2ChestInstance && activeChar.isPlayer())
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);

		if(!target.isDoor() || _unlockPower == 0)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		L2DoorInstance door = (L2DoorInstance) target;

		if(door.isOpen())
		{
			activeChar.sendPacket(Msg.IT_IS_NOT_LOCKED);
			return false;
		}

		if(!door.isUnlockable() || door.getSiegeUnit() != null)
		{
			activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
			return false;
		}

		if(door.key > 0) // ключ не подходит к двери
		{
			activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
			return false;
		}

		if(_unlockPower - door.getLevel() * 100 < 0) // Дверь слишком высокого уровня
		{
			activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character targ : targets)
			if(targ != null)
			{
				if(targ.isDoor())
				{
					L2DoorInstance target = (L2DoorInstance) targ;
					if(!target.isOpen() && (target.key > 0 || Rnd.chance(_unlockPower - target.level * 100)))
					{
						target.openMe();
						target.onOpen();
					}
					else
						activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
					return;
				}
				else if(targ instanceof L2ChestInstance)
				{
					L2ChestInstance target = (L2ChestInstance)targ;
					if(!target.isDead())
						target.tryOpen((L2Player)activeChar, this);
				}
			}
	}
}