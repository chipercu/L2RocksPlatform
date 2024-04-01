package com.fuzzy.subsystem.gameserver.tables;

import com.fuzzy.subsystem.database.DatabaseUtils;
import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.gameserver.idfactory.IdFactory;
import com.fuzzy.subsystem.gameserver.model.L2Territory;
import com.fuzzy.subsystem.gameserver.model.instances.L2DoorInstance;
import com.fuzzy.subsystem.gameserver.templates.L2CharTemplate;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoorTable
{
	private static final Logger _log = Logger.getLogger(DoorTable.class.getName());

	private HashMap<Integer, L2DoorInstance> _staticItems;
	private HashMap<String, L2DoorInstance> _doors;

	private static DoorTable _instance;

	public static DoorTable getInstance()
	{
		if(_instance == null)
			new DoorTable();
		return _instance;
	}

	public DoorTable()
	{
		_instance = this;
		_staticItems = new HashMap<Integer, L2DoorInstance>();
		_doors = new HashMap<String, L2DoorInstance>();
		RestoreDoors();
	}

	public void respawn()
	{
		for(L2DoorInstance door : _staticItems.values())
			if(door != null)
				door.decayMe();
		_instance = new DoorTable();
	}

	private void RestoreDoors()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM doors");
			rs = statement.executeQuery();
			FillDoors(rs);
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Cannot load doors");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	private void FillDoors(ResultSet DoorData) throws Exception
	{
		StatsSet baseDat = new StatsSet();
		baseDat.set("level", 0);
		baseDat.set("jClass", "door");
		baseDat.set("baseSTR", 0);
		baseDat.set("baseCON", 0);
		baseDat.set("baseDEX", 0);
		baseDat.set("baseINT", 0);
		baseDat.set("baseWIT", 0);
		baseDat.set("baseMEN", 0);
		baseDat.set("baseShldDef", 0);
		baseDat.set("baseShldRate", 0);
		baseDat.set("baseAccCombat", 38);
		baseDat.set("baseEvasRate", 38);
		baseDat.set("baseCritRate", 38);
		baseDat.set("collision_radius", 5);
		baseDat.set("collision_height", 0);
		baseDat.set("sex", "male");
		baseDat.set("type", "");
		baseDat.set("baseAtkRange", 0);
		baseDat.set("baseMpMax", 0);
		baseDat.set("baseCpMax", 0);
		baseDat.set("revardExp", 0);
		baseDat.set("revardSp", 0);
		baseDat.set("basePAtk", 0);
		baseDat.set("baseMAtk", 0);
		baseDat.set("basePAtkSpd", 0);
		baseDat.set("aggroRange", 0);
		baseDat.set("baseMAtkSpd", 0);
		baseDat.set("rhand", 0);
		baseDat.set("lhand", 0);
		baseDat.set("armor", 0);
		baseDat.set("baseWalkSpd", 0);
		baseDat.set("baseRunSpd", 0);
		baseDat.set("baseHpReg", 0);
		baseDat.set("baseCpReg", 0);
		baseDat.set("baseMpReg", 0);
		baseDat.set("siege_weapon", false);
		baseDat.set("geodata", true);

		StatsSet npcDat;
		while(DoorData.next())
		{
			npcDat = baseDat.clone();
			int id = DoorData.getInt("id");
			int zmin = DoorData.getInt("minz");
			int zmax = DoorData.getInt("maxz");
			int posx = DoorData.getInt("posx");
			int posy = DoorData.getInt("posy");
			String doorname = DoorData.getString("name");
			String pts_name = DoorData.getString("pts_name");

			npcDat.set("npcId", id);
			npcDat.set("name", doorname);
			npcDat.set("baseHpMax", DoorData.getInt("hp"));
			npcDat.set("basePDef", DoorData.getInt("pdef"));
			npcDat.set("baseMDef", DoorData.getInt("mdef"));

			L2CharTemplate template = new L2CharTemplate(npcDat);
			L2DoorInstance door = new L2DoorInstance(IdFactory.getInstance().getNextId(), template, id, doorname, DoorData.getBoolean("unlockable"), DoorData.getBoolean("showHp"));
			door.pts_name = pts_name;
			_staticItems.put(id, door);
			_doors.put(pts_name, door);
			L2Territory pos = new L2Territory(id);
			door.setGeoPos(pos);
			pos.add(DoorData.getInt("ax"), DoorData.getInt("ay"), zmin, zmax);
			pos.add(DoorData.getInt("bx"), DoorData.getInt("by"), zmin, zmax);
			pos.add(DoorData.getInt("cx"), DoorData.getInt("cy"), zmin, zmax);
			pos.add(DoorData.getInt("dx"), DoorData.getInt("dy"), zmin, zmax);
			door.getTemplate().collisionHeight = zmax - zmin & 0xfff0;
			door.getTemplate().collisionRadius = Math.max(50, Math.min(posx - pos.getXmin(), posy - pos.getYmin()));
			/*if(door.getTemplate().collisionRadius > 200)
				System.out.println("DoorId: " + id + ", collision: " + door.getTemplate().collisionRadius + ", posx: " + posx + ", posy: " + posy + ", xMin: " + pos.getXmin() + ", yMin: " + pos.getYmin());
			if(pos.getXmin() == pos.getXmax())
				_log.warning("door " + id + " has zero size");
			else if(pos.getYmin() == pos.getYmax())
				_log.warning("door " + id + " has zero size");*/
			door.setXYZInvisible(posx, posy, zmin + 32);
			door.setCurrentHpMp(door.getMaxHp(), door.getMaxMp(), true);
			door.setOpen(false);
			door.level = DoorData.getByte("level");
			door.key = DoorData.getInt("key");

			// Дверь/стена может быть атакована только осадным оружием
			door.setSiegeWeaponOlnyAttackable(DoorData.getBoolean("siege_weapon"));
			door.spawnMe(door.getLoc());

			checkOpenOrClose();
		}
		_log.info("DoorTable: Loaded " + _staticItems.size() + " doors.");
	}

	public boolean isInitialized()
	{
		return _initialized;
	}

	private boolean _initialized = true;

	public L2DoorInstance getDoor(Integer id)
	{
		return _staticItems.get(id);
	}

	public L2DoorInstance getDoor(String id)
	{
		return _doors.get(id);
	}

	public void putDoor(Integer id, L2DoorInstance door)
	{
		_staticItems.put(id, door);
		_doors.put(door.pts_name, door);
	}

	public void removeDoor(Integer id)
	{
		_staticItems.remove(id);
	}

	public L2DoorInstance[] getDoors()
	{
		return _staticItems.values().toArray(new L2DoorInstance[_staticItems.size()]);
	}

	/**
	 * Performs a check and sets up a scheduled task for
	 * those doors that require auto opening/closing.
	 */
	public void checkAutoOpen()
	{
		for(L2DoorInstance doorInst : getDoors())

			// Garden of Eva (every 7 minutes)
			if(doorInst.getDoorName().startsWith("Eva_"))
				doorInst.setAutoActionDelay(420000);

			// Tower of Insolence (every 5 minutes)
			else if(doorInst.getDoorName().startsWith("hubris_"))
				doorInst.setAutoActionDelay(300000);

			// Giran Devil island (every 5 minutes)
			else if(doorInst.getDoorName().startsWith("Devil.opendoor"))
				doorInst.setAutoActionDelay(300000);

			// Coral Garden Gate (every 15 minutes)
			else if(doorInst.getDoorName().startsWith("Coral_garden"))
				doorInst.setAutoActionDelay(900000);

			// Normil's cave (every 5 minutes)
			else if(doorInst.getDoorName().startsWith("Normils_cave"))
				doorInst.setAutoActionDelay(300000);
	}

	public void checkOpenOrClose()
	{
		for(L2DoorInstance door : getDoors())
		{
			if(door.getDoorId() == 20250004 || door.getDoorId() == 20250005 || door.getDoorId() == 20250006 || door.getDoorId() == 20250007 || door.getDoorId() == 20250008 || door.getDoorId() == 20250009 || door.getDoorId() == 18250025 || door.getDoorId() == 19260054 || door.getDoorId() == 19260053 || door.getDoorId() == 20260003 || door.getDoorId() == 20260004)
	        	door.openMe();
		}
	}
}