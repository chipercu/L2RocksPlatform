package quests._1201_DarkCloudMansion;

import gnu.trove.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncMul;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Rnd;

import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class _1201_DarkCloudMansion extends Quest implements ScriptFile
{
	// Items
	private static final int CC = 9690; // Contaminated Crystal

	// NPC
	private static final int YIYEN = 32282;
	private static final int SOFaith = 32288; // Symbol of Faith
	private static final int SOAdversity = 32289; // Symbol of Adversity
	private static final int SOAdventure = 32290; // Symbol of Anventure
	private static final int SOTruth = 32291; // Symbol of Truth
	private static final int BSM = 32324; // Black Stone Monolith
	private static final int SC = 22402; // Shadow Column

	// Mobs
	private static final int[] CCG = new int[] { 18369, 18370 }; // Chromatic Crystal Golem
	private static final int[] BM = new int[] { 22272, 22273, 22274 }; // Beleth's Minions
	private static final int[] HG = new int[] { 22264, 22265 }; // [22318,22319] // Hall Guards
	private static final int[] BS = new int[] { 18371, 18372, 18373, 18374, 18375, 18376, 18377 }; // Beleth's Samples

	// Doors/Walls
	private static final int D1 = 24230001; // Starting Room
	private static final int D2 = 24230002; // First Room
	private static final int D3 = 24230005; // Second Room
	private static final int D4 = 24230003; // Third Room
	private static final int D5 = 24230004; // Forth Room
	private static final int D6 = 24230006; // Fifth Room
	private static final int W1 = 24230007; // Wall 1
	/*
	private static final int W2 = 24230008; // Wall 2
	private static final int W3 = 24230009; // Wall 3
	private static final int W4 = 24230010; // Wall 4
	private static final int W5 = 24230011; // Wall 5
	private static final int W6 = 24230012; // Wall 6
	private static final int W7 = 24230013; // Wall 7
	*/

	// Second room - random monolith order
	private static final int[][] order = new int[][] { { 1, 2, 3, 4, 5, 6 }, { 6, 5, 4, 3, 2, 1 }, { 4, 5, 6, 3, 2, 1 },
			{ 2, 6, 3, 5, 1, 4 }, { 4, 1, 5, 6, 2, 3 }, { 3, 5, 1, 6, 2, 4 }, { 6, 1, 3, 4, 5, 2 }, { 5, 6, 1, 2, 4, 3 },
			{ 5, 2, 6, 3, 4, 1 }, { 1, 5, 2, 6, 3, 4 }, { 1, 2, 3, 6, 5, 4 }, { 6, 4, 3, 1, 5, 2 }, { 3, 5, 2, 4, 1, 6 },
			{ 3, 2, 4, 5, 1, 6 }, { 5, 4, 3, 1, 6, 2 } };

	// Second room - golem spawn locatons - random    
	private static final int[][] golems = new int[][] { { CCG[0], 148060, 181389 }, { CCG[1], 147910, 181173 },
			{ CCG[0], 147810, 181334 }, { CCG[1], 147713, 181179 }, { CCG[0], 147569, 181410 }, { CCG[1], 147810, 181517 },
			{ CCG[0], 147805, 181281 } };

	// forth room - random shadow column
	private static final int[][] rows = new int[][] { { 1, 1, 0, 1, 0 }, { 0, 1, 1, 0, 1 }, { 1, 0, 1, 1, 0 },
			{ 0, 1, 0, 1, 1 }, { 1, 0, 1, 0, 1 } };

	// Fifth room - beleth order
	private static final int[][] beleths = new int[][] { { 1, 0, 1, 0, 1, 0, 0 }, { 0, 0, 1, 0, 1, 1, 0 },
			{ 0, 0, 0, 1, 0, 1, 1 }, { 1, 0, 1, 1, 0, 0, 0 }, { 1, 1, 0, 0, 0, 1, 0 }, { 0, 1, 0, 1, 0, 1, 0 },
			{ 0, 0, 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 0, 1, 0 }, { 0, 1, 1, 0, 0, 0, 1 } };

	public class World
	{
		public int instanceId;
		public int status;
		public Room StartRoom;
		public Room Hall;
		public Room FirstRoom;
		public Room SecondRoom;
		public Room ThirdRoom;
		public Room ForthRoom;
		public Room FifthRoom;
	}

	public class Room
	{
		public Map<L2NpcInstance, Boolean> npclist;
		public List<int[]> npclist2;
		public List<int[]> monolith;
		public int[] monolithOrder;
		public List<int[]> belethOrder;
		public int counter;
	}

	private static TIntObjectHashMap<World> worlds = new TIntObjectHashMap<World>();

	public void onLoad()
	{
		ScriptFile._log.info("Loaded Quest: 1201: Dark Cloud Mansion");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _1201_DarkCloudMansion()
	{
		super("Dark Cloud Mansion", true);

		addStartNpc(YIYEN);
		addTalkId(SOTruth);
		addFirstTalkId(BSM);
		addAttackId(SC);
		addAttackId(BS);
		addKillId(BS);
		addKillId(BM);
		addKillId(CCG);
		addKillId(SC);
		addKillId(HG);

		//addKillId(22318);
		//addKillId(22319);
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		World world = worlds.get(player.getReflection().getId());
		if(world.status == 4)
		{
			for(int[] npcObj : world.SecondRoom.monolith)
				if(npcObj[0] == npc.getObjectId())
					checkStone(npc, world.SecondRoom.monolithOrder, npcObj, world);
			if(allStonesDone(world))
			{
				removeMonoliths(world);
				runHall3(world);
			}
		}
		return null;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		L2Player player = st.getPlayer();
		if(npcId == YIYEN)
		{
			st.setState(STARTED);
			enterInstance(player);
			return null;
		}
		if(npc.getReflection().getId() == 0)
			return null;
		World world = worlds.get(npc.getReflection().getId());
		if(world != null)
			if(npcId == SOTruth)
			{
				player.setReflection(0);
				player.teleToLocation(new Location(139968, 150367, -3111));
				if(npc.OwnItemCount(player,9690) == 0)
					npc.GiveItem1(player,9690,1);
			}
		return null;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		World world = worlds.get(npc.getReflection().getId());
		if(world == null)
			return null;

		switch(world.status)
		{
			case 0:
				if(checkKillProgress(npc, world.StartRoom))
					runHall(world);
				break;
			case 1:
				if(checkKillProgress(npc, world.Hall))
					runFirstRoom(world);
				break;
			case 2:
				if(checkKillProgress(npc, world.FirstRoom))
					runHall2(world);
				break;
			case 3:
				if(checkKillProgress(npc, world.Hall))
					runSecondRoom(world);
				break;
			case 5:
				if(checkKillProgress(npc, world.Hall))
					runThirdRoom(world);
				break;
			case 6:
				if(checkKillProgress(npc, world.ThirdRoom))
					runForthRoom(world);
				break;
			case 7:
				chkShadowColumn(world, npc);
				break;
			case 8:
				BelethSampleKilled(world, npc, player);
				break;
		}
		return null;
	}

	@Override
	public String onAttack(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		World world = worlds.get(player.getReflection().getId());
		if(world != null && world.status == 7)
			for(int[] mob : world.ForthRoom.npclist2)
				if(mob[0] == npc.getObjectId())
					if(Rnd.chance(12) && npc.isBusy())
						addSpawnToInstance(BM[Rnd.get(BM.length)], player.getLoc(), 100, world.instanceId);

		if(world != null && world.status == 8)
			BelethSampleAttacked(world, npc, player);
		return null;
	}

	private void endInstance(World world)
	{
		world.status = 9;
		addSpawnToInstance(SOTruth, new Location(148911, 181940, -6117, 16383), 0, world.instanceId);
		world.StartRoom = null;
		world.Hall = null;
		world.SecondRoom = null;
		world.ThirdRoom = null;
		world.ForthRoom = null;
		world.FifthRoom = null;
	}

	private void enterInstance(L2Player player)
	{
		int instancedZoneId = 9;
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);

		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		String name = il.getName();
		int timelimit = il.getTimelimit();
		int min_level = il.getMinLevel();
		int max_level = il.getMaxLevel();
		int minParty = il.getMinParty();
		int maxParty = il.getMaxParty();

		if(minParty > 1 && !player.isInParty())
		{
			player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
			return;
		}

		if(player.isInParty())
		{
			// TODO возможно, лучше запретить повторный вход "вылетевших"
			if(player.getParty().isInReflection())
			{
				Reflection old_ref = ReflectionTable.getInstance().get(player.getParty().getReflection().getId());
				if(old_ref != null && worlds.containsKey(old_ref.getId()) && player.getActiveReflection() == old_ref)
				{
					player.setReflection(old_ref);
					player.teleToLocation(old_ref.getTeleportLoc());
				}
				return;
			}

			for(L2Player member : player.getParty().getPartyMembers())
				if(ilm.getTimeToNextEnterInstance(name, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}

			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
				return;
			}

			if(player.getParty().getMemberCount() > maxParty)
			{
				player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return;
			}

			for(L2Player member : player.getParty().getPartyMembers())
			{
				if(member.getLevel() < min_level || member.getLevel() > max_level)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
					member.sendPacket(sm);
					player.sendPacket(sm);
					return;
				}
				if(member.isCursedWeaponEquipped())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(!player.isInRange(member, 500))
				{
					member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					return;
				}
			}
		}

		Reflection r = new Reflection(name);
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			if(i.getDoors() != null)
				for(L2DoorInstance d : i.getDoors())
				{
					L2DoorInstance door = d.clone();
					r.addDoor(door);
					door.setReflection(r);
					door.spawnMe();
					if(d.isOpen())
						door.openMe();
				}
		}

		World world = new World();
		world.instanceId = r.getId();
		worlds.put(r.getId(), world);
		runStartRoom(world);

		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(member != player)
				newQuestState(member, STARTED);
			member.setReflection(r);
			member.teleToLocation(il.getTeleportCoords());
			member.setVar("backCoords", r.getReturnLoc().toXYZString());
			member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
		}

		player.getParty().setReflection(r);
		r.setParty(player.getParty());
		r.startCollapseTimer(timelimit * 60 * 1000L);
		player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
	}

	private void runStartRoom(World world)
	{
		world.status = 0;
		world.StartRoom = new Room();
		world.StartRoom.npclist = new HashMap<L2NpcInstance, Boolean>();
		L2NpcInstance newNpc;
		newNpc = addSpawnToInstance(BM[0], new Location(146817, 180335, -6117), 0, world.instanceId);
		world.StartRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[0], new Location(146741, 180589, -6117), 0, world.instanceId);
		world.StartRoom.npclist.put(newNpc, false);
	}

	private void spawnHall(World world)
	{
		world.Hall = new Room();
		world.Hall.npclist = new HashMap<L2NpcInstance, Boolean>();
		L2NpcInstance newNpc = addSpawnToInstance(BM[1], new Location(147217, 180112, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[2], new Location(147217, 180209, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[1], new Location(148521, 180112, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[0], new Location(148521, 180209, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[1], new Location(148525, 180910, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[2], new Location(148435, 180910, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[1], new Location(147242, 180910, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[2], new Location(147242, 180819, -6117), 0, world.instanceId);
		world.Hall.npclist.put(newNpc, false);
	}

	private void runHall(World world)
	{
		world.status = 1;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D1);
		spawnHall(world);
	}

	private void runFirstRoom(World world)
	{
		world.status = 2;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D2);
		world.FirstRoom = new Room();
		world.FirstRoom.npclist = new HashMap<L2NpcInstance, Boolean>();
		L2NpcInstance newNpc = addSpawnToInstance(HG[1], new Location(147842, 179837, -6117), 0, world.instanceId);
		world.FirstRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(HG[0], new Location(147711, 179708, -6117), 0, world.instanceId);
		world.FirstRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(HG[1], new Location(147842, 179552, -6117), 0, world.instanceId);
		world.FirstRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(HG[0], new Location(147964, 179708, -6117), 0, world.instanceId);
		world.FirstRoom.npclist.put(newNpc, false);
	}

	private void runHall2(World world)
	{
		world.status = 3;
		spawnHall(world);
	}

	private void runSecondRoom(World world)
	{
		L2NpcInstance newNpc = addSpawnToInstance(SOFaith, new Location(147818, 179643, -6117), 0, world.instanceId);
		world.status = 4;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D3);
		world.SecondRoom = new Room();
		world.SecondRoom.monolith = new ArrayList<int[]>();
		int i = Rnd.get(order.length);
		world.SecondRoom.monolithOrder = new int[] { 1, 0, 0, 0, 0, 0, 0 };
		newNpc = addSpawnToInstance(BSM, new Location(147800, 181150, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][0], 0 });
		newNpc = addSpawnToInstance(BSM, new Location(147900, 181215, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][1], 0 });
		newNpc = addSpawnToInstance(BSM, new Location(147900, 181345, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][2], 0 });
		newNpc = addSpawnToInstance(BSM, new Location(147800, 181410, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][3], 0 });
		newNpc = addSpawnToInstance(BSM, new Location(147700, 181345, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][4], 0 });
		newNpc = addSpawnToInstance(BSM, new Location(147700, 181215, -6117), 0, world.instanceId);
		world.SecondRoom.monolith.add(new int[] { newNpc.getObjectId(), order[i][5], 0 });
	}

	private void runHall3(World world)
	{
		addSpawnToInstance(SOAdversity, new Location(147808, 181281, -6117, 16383), 0, world.instanceId);
		world.status = 5;
		spawnHall(world);
	}

	private void runThirdRoom(World world)
	{
		world.status = 6;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D4);
		world.ThirdRoom = new Room();
		world.ThirdRoom.npclist = new HashMap<L2NpcInstance, Boolean>();
		L2NpcInstance newNpc = addSpawnToInstance(BM[1], new Location(148765, 180450, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[2], new Location(148865, 180190, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[1], new Location(148995, 180190, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[0], new Location(149090, 180450, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[1], new Location(148995, 180705, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BM[2], new Location(148865, 180705, -6117), 0, world.instanceId);
		world.ThirdRoom.npclist.put(newNpc, false);
	}

	private void runForthRoom(World world)
	{
		world.status = 7;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D5);
		world.ForthRoom = new Room();
		world.ForthRoom.npclist2 = new ArrayList<int[]>();
		world.ForthRoom.counter = 0;

		int[] temp = new int[7];
		int[][] templist = new int[7][];

		for(int i = 0; i < temp.length; i++)
			temp[i] = Rnd.get(rows.length);

		for(int i = 0; i < temp.length; i++)
			templist[i] = rows[temp[i]];

		int xx = 0;
		int yy = 0;

		for(int x = 148660; x <= 149160; x += 125)
		{
			yy = 0;
			for(int y = 179280; y >= 178530; y -= 125)
			{
				L2NpcInstance newNpc = addSpawnToInstance(SC, new Location(x, y, -6115, 16215), 0, world.instanceId);
				newNpc.setAI(new L2CharacterAI(newNpc));
				if(templist[yy][xx] == 0)
				{
					newNpc.setBusy(true); // Используется здесь для определения "ненастощих" статуй.
					newNpc.addStatFunc(new FuncMul(Stats.p_magical_defence, 0x30, this, 1000));
					newNpc.addStatFunc(new FuncMul(Stats.p_physical_defence, 0x30, this, 1000));
				}

				world.ForthRoom.npclist2.add(new int[] { newNpc.getObjectId(), templist[yy][xx], yy });
				yy += 1;
			}
			xx += 1;
		}
	}

	private void runFifthRoom(World world)
	{
		world.status = 8;
		ReflectionTable.getInstance().get(world.instanceId).openDoor(D6);
		world.FifthRoom = new Room();
		addSpawnToInstance(SOAdventure, new Location(148910, 178397, -6117, 16383), 0, world.instanceId);
		spawnBelethSample(world);
	}

	private void spawnBelethSample(World world)
	{
		world.FifthRoom.npclist2 = new ArrayList<int[]>();
		int[] beleth = beleths[Rnd.get(beleths.length)];
		world.FifthRoom.belethOrder = new ArrayList<int[]>();
		world.FifthRoom.belethOrder.add(beleth);
		int idx = 0;
		for(int x = 148720; x <= 149110; x += 65)
		{
			L2NpcInstance newNpc = addSpawnToInstance(BS[idx], new Location(x, 182145, -6117, 48810), 0, world.instanceId);
			world.FifthRoom.npclist2.add(new int[] { newNpc.getObjectId(), idx, beleth[idx] });
			idx += 1;
		}
	}

	private boolean checkKillProgress(L2NpcInstance npc, Room room)
	{
		if(room.npclist.containsKey(npc))
			room.npclist.put(npc, true);
		for(boolean value : room.npclist.values())
			if(!value)
				return false;
		return true;
	}

	private void spawnRndGolem(World world)
	{
		int count = 0;
		for(L2MonsterInstance mon : ReflectionTable.getInstance().get(world.instanceId).getMonsters())
			if(mon != null && !mon.isDead() && (mon.getNpcId() == 18369 || mon.getNpcId() == 18370))
				count++;
		if(count < 36)
		{
			int i = Rnd.get(golems.length);
			int id = golems[i][0];
			int x = golems[i][1];
			int y = golems[i][2];
			addSpawnToInstance(id, new Location(x, y, -6117), 0, world.instanceId);
		}
	}

	private void checkStone(L2NpcInstance npc, int[] order, int[] npcObj, World world)
	{
		for(int i = 1; i <= 6; i++)
			if(order[i] == 0 && order[i - 1] != 0)
				if(npcObj[1] == i && npcObj[2] == 0)
				{
					order[i] = 1;
					npcObj[2] = 1;
					npc.broadcastSkill(new MagicSkillUse(npc, npc, 5441, 1, 1, 0));
					return;
				}
		spawnRndGolem(world);
	}

	private void BelethSampleAttacked(World world, L2NpcInstance npc, L2Player player)
	{
		for(int[] list : world.FifthRoom.npclist2)
			if(list[0] == npc.getObjectId())
			{
				if(list[2] == 1)
				{
					Functions.npcSay(npc, "You have done well!");
					npc.deleteMe();
					world.FifthRoom.counter += 1;
					if(world.FifthRoom.counter >= 3)
					{
						unspawnBelethSample(world);
						endInstance(world);
						return;
					}
				}
				else
					world.FifthRoom.counter = 0;
				return;
			}
	}

	private void BelethSampleKilled(World world, L2NpcInstance npc, L2Player player)
	{
		for(int[] list : world.FifthRoom.npclist2)
			if(list[0] == npc.getObjectId())
			{
				world.FifthRoom.counter = 0;
				unspawnBelethSample(world);
				spawnBelethSample(world);
				return;
			}
	}

	private void unspawnBelethSample(World world)
	{
		for(int[] list : world.FifthRoom.npclist2)
		{
			L2NpcInstance npc = L2ObjectsStorage.getNpc(list[0]);
			if(npc != null)
				npc.deleteMe();
		}
	}

	private void removeMonoliths(World world)
	{
		for(int[] list : world.SecondRoom.monolith)
		{
			L2NpcInstance npc = L2ObjectsStorage.getNpc(list[0]);
			if(npc != null)
				npc.deleteMe();
		}
	}

	private boolean allStonesDone(World world)
	{
		for(int[] list : world.SecondRoom.monolith)
			if(list[2] != 1)
				return false;
		return true;
	}

	private void chkShadowColumn(World world, L2NpcInstance npc)
	{
		Reflection ref = ReflectionTable.getInstance().get(world.instanceId);
		for(int[] mob : world.ForthRoom.npclist2)
			if(mob[0] == npc.getObjectId())
				for(int i = 0; i <= 7; i++)
					if(mob[2] == i && world.ForthRoom.counter == i)
					{
						ref.openDoor(W1 + i);
						world.ForthRoom.counter += 1;
						if(world.ForthRoom.counter == 7)
							runFifthRoom(world);
					}
	}
}