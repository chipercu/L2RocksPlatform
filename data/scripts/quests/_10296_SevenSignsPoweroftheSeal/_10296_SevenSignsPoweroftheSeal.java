package quests._10296_SevenSignsPoweroftheSeal;

import javolution.util.FastMap;
import l2open.common.*;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.util.Location;

/**
 * @author pchayka
 */
public class _10296_SevenSignsPoweroftheSeal extends Quest implements ScriptFile
{
	private static final int Eris = 32792;
	private static final int ElcardiaInzone1 = 32787;
	private static final int EtisEtina = 18949;
	private static final int ElcardiaHome = 32784;
	private static final int Hardin = 30832;
	private static final int Wood = 32593;
	private static final int Franz = 32597;

	private static final Location hiddenLoc = new Location(120744, -87432, -3392);

	public _10296_SevenSignsPoweroftheSeal()
	{
		super(false);
		addStartNpc(Eris);
		addTalkId(ElcardiaInzone1, ElcardiaHome, Hardin, Wood, Franz);
		addKillId(EtisEtina);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("eris_q10296_3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("start_scene"))
		{
			st.setCond(2);
			teleportElcardia(player, hiddenLoc);
			ThreadPoolManager.getInstance().schedule(new Teleport(player), 60500L);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_OPENING);
			return null;
		}
		else if(event.equalsIgnoreCase("teleport_back"))
		{
			player.teleToLocation(new Location(76736, -241021, -10832));
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("elcardiahome_q10296_3.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("hardin_q10296_3.htm"))
		{
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("enter_instance"))
		{
			enterInstance(player, 157);
			return null;
		}
		else if(event.equalsIgnoreCase("franz_q10296_3.htm"))
		{
			if(player.getLevel() >= 81)
			{
				st.addExpAndSp(125000000, 12500000);
				st.giveItems(17265, 1);
				st.setState(COMPLETED);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
				htmltext = "franz_q10296_0.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		L2Player player = st.getPlayer();
		if(player.isSubClassActive())
			return "no_subclass_allowed.htm";

		if(npcId == Eris)
		{
			if(cond == 0)
			{
				QuestState qs = player.getQuestState("_10295_SevenSignsSolinasTomb");
				if(player.getLevel() >= 81 && qs != null && qs.isCompleted())
					htmltext = "eris_q10296_1.htm";
				else
				{
					htmltext = "eris_q10296_0.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "eris_q10296_4.htm";
			else if(cond == 2)
				htmltext = "eris_q10296_5.htm";
			else if(cond >= 3)
				htmltext = "eris_q10296_6.htm";
		}
		else if(npcId == ElcardiaInzone1)
		{
			if(cond == 1)
				htmltext = "elcardia_q10296_1.htm";
			else if(cond == 2)
			{
				if(st.getInt("EtisKilled") == 0)
					htmltext = "elcardia_q10296_1.htm";
				else
				{
					st.setCond(3);
					htmltext = "elcardia_q10296_2.htm";
				}
			}
			else if(cond >= 3)
				htmltext = "elcardia_q10296_4.htm";
		}
		else if(npcId == ElcardiaHome)
		{
			if(cond == 3)
				htmltext = "elcardiahome_q10296_1.htm";
			else if(cond >= 4)
				htmltext = "elcardiahome_q10296_3.htm";
		}
		else if(npcId == Hardin)
		{
			if(cond == 4)
				htmltext = "hardin_q10296_1.htm";
			else if(cond == 5)
				htmltext = "hardin_q10296_4.htm";
		}
		else if(npcId == Wood)
		{
			if(cond == 5)
				htmltext = "wood_q10296_1.htm";
		}
		else if(npcId == Franz)
		{
			if(cond == 5)
				htmltext = "franz_q10296_1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == EtisEtina)
		{
			st.set("EtisKilled", 1);
			L2NpcInstance n = st.getPlayer().getReflection().findFirstNPC(ElcardiaInzone1);
			n.teleToLocation(new Location(120664, -86968, -3392));
			ThreadPoolManager.getInstance().schedule(new ElcardiaTeleport(st.getPlayer()), 60500L);
			st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_CLOSING);

		}
		return null;
	}

	private void teleportElcardia(L2Player player)
	{
		L2NpcInstance n = player.getReflection().findFirstNPC(ElcardiaInzone1);
		n.teleToLocation(Location.findPointToStay(player, 60));
		if(n.isBlocked())
			n.unblock();
	}

	private void teleportElcardia(L2Player player, Location loc)
	{
		L2NpcInstance n = player.getReflection().findFirstNPC(ElcardiaInzone1);
		n.teleToLocation(loc);
		n.block();
	}

	private class Teleport extends RunnableImpl
	{
		L2Player _player;

		public Teleport(L2Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			_player.teleToLocation(new Location(76736, -241021, -10832));
			teleportElcardia(_player);
		}
	}

	private class ElcardiaTeleport extends RunnableImpl
	{
		L2Player _player;

		public ElcardiaTeleport(L2Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			teleportElcardia(_player);
		}
	}

	private void enterInstance(L2Player player, int instancedZoneId)
	{
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		Reflection r = new Reflection(il.getName());
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
		}

		int timelimit = il.getTimelimit();

		player.setReflection(r);
		player.teleToLocation(-23743, -8947, -5384);
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.startCollapseTimer(timelimit * 60 * 1000L);
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}