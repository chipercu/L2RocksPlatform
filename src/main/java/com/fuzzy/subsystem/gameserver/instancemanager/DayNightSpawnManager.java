package com.fuzzy.subsystem.gameserver.instancemanager;

import com.fuzzy.subsystem.gameserver.GameTimeController;
import com.fuzzy.subsystem.gameserver.model.L2Spawn;
import com.fuzzy.subsystem.util.GArray;

public class DayNightSpawnManager
{
	private static DayNightSpawnManager _instance;
	private static GArray<L2Spawn> _dayMobs = new GArray<L2Spawn>();
	private static GArray<L2Spawn> _nightMobs = new GArray<L2Spawn>();

	public static DayNightSpawnManager getInstance()
	{
		if(_instance == null)
			_instance = new DayNightSpawnManager();
		return _instance;
	}

	public void addDayMob(L2Spawn spawnDat)
	{
		_dayMobs.add(spawnDat);
	}

	public void addNightMob(L2Spawn spawnDat)
	{
		_nightMobs.add(spawnDat);
	}

	public void deleteMobs(GArray<L2Spawn> mobsSpawnsList)
	{
		for(L2Spawn spawnDat : mobsSpawnsList)
			spawnDat.despawnAll();
	}

	public void spawnMobs(GArray<L2Spawn> mobsSpawnsList)
	{
		for(L2Spawn spawnDat : mobsSpawnsList)
			spawnDat.init();
	}

	public void changeMode(int mode)
	{
		switch(mode)
		{
			case 1: // day spawns
				deleteMobs(_nightMobs);
				deleteMobs(_dayMobs);
				spawnMobs(_dayMobs);
				break;
			case 2: // night spawns
				deleteMobs(_nightMobs);
				deleteMobs(_dayMobs);
				spawnMobs(_nightMobs);
				break;
		}
	}

	public void notifyChangeMode()
	{
		if(GameTimeController.getInstance().isNowNight())
			changeMode(2);
		else
			changeMode(1);
	}

	public void cleanUp()
	{
		deleteMobs(_nightMobs);
		deleteMobs(_dayMobs);

		_nightMobs.clear();
		_dayMobs.clear();
	}
}