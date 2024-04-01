package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Location;
import com.fuzzy.subsystem.util.reference.*;

public final class L2EventTrapInstance extends L2TrapInstance
{
	public L2EventTrapInstance(int objectId, L2NpcTemplate template, L2Character owner, Location loc)
	{
		super(objectId, template, owner, null, loc, true);
	}

	public void detonate(L2Character target)
	{
		if(getOwner() != null)
			getOwner().sendMessage("В вашу ловушку кто-то попался.");
		target.sendMessage("Вы попали в ловушку. Ваше местонахождение известно другим игрокам.");
		destroy();
	}

	public void destroy()
	{
		L2World.removeTerritory(_territory);
		//deleteMe();
		doDie(this);
		if(_destroyTask != null)
			_destroyTask.cancel(false);
		_destroyTask = null;
	}

	public boolean isDetected()
	{
		return false;
	}
}