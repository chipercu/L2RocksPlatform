package com.fuzzy.subsystem.gameserver.instancemanager;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.gameserver.model.L2Zone;
import com.fuzzy.subsystem.gameserver.model.L2Zone.ZoneType;
import com.fuzzy.subsystem.gameserver.model.entity.Town;
import com.fuzzy.subsystem.gameserver.tables.MapRegion;
import com.fuzzy.subsystem.util.GArray;

import java.util.logging.Logger;

public final class TownManager
{
	private static TownManager _instance;
	private GArray<Town> _towns = new GArray<Town>();

	private final static Logger _log = Logger.getLogger(TownManager.class.getName());

	private TownManager()
	{
		GArray<L2Zone> zones = ZoneManager.getInstance().getZoneByType(ZoneType.Town);
		if(zones.size() == 0)
			_log.warning("Not found zones for Towns!!!");
		else
			for(L2Zone zone : zones)
				_towns.add(new Town(zone.getIndex()));
	}

	public static TownManager getInstance()
	{
		if(_instance == null)
			_instance = new TownManager();
		return _instance;
	}

	public Town getClosestTown(int x, int y)
	{
		return getTown(MapRegion.getInstance().getMapRegion(x, y));
	}

	public Town getClosestTown(L2Object activeObject)
	{
		return getTown(MapRegion.getInstance().getMapRegion(activeObject.getX(), activeObject.getY()));
	}

	public int getClosestTownNumber(L2Character activeChar)
	{
		return MapRegion.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
	}

	public String getClosestTownName(L2Character activeChar)
	{
		return getClosestTown(activeChar).getName();
	}

	public Town getTown(int townId)
	{
		for(Town town : _towns)
			if(town.getTownId() == townId)
				return town;

		return null;
	}
}