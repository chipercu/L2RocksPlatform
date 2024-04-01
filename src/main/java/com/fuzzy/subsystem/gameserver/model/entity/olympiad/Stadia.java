package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

import com.fuzzy.subsystem.gameserver.instancemanager.ZoneManager;
import com.fuzzy.subsystem.gameserver.model.L2Zone;
import com.fuzzy.subsystem.gameserver.model.L2Zone.ZoneType;

public class Stadia
{
	private int _olympiadStadiaId = 0;
	private L2Zone _Zone;

	public Stadia(int olympiadStadiaId)
	{
		_olympiadStadiaId = olympiadStadiaId;
	}

	public final int getOlympiadStadiaId()
	{
		return _olympiadStadiaId;
	}

	public final L2Zone getZone()
	{
		if(_Zone == null)
			_Zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.OlympiadStadia, _olympiadStadiaId, true);
		return _Zone;
	}
}