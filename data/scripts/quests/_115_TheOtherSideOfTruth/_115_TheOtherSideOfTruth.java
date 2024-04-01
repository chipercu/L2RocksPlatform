package quests._115_TheOtherSideOfTruth;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _115_TheOtherSideOfTruth extends Quest implements ScriptFile
{
	// NPCs
	private static int Rafforty = 32020;
	private static int Misa = 32018;
	private static int Kierre = 32022;
	private static int Ice_Sculpture1 = 32021;
	private static int Ice_Sculpture2 = 32077;
	private static int Ice_Sculpture3 = 32078;
	private static int Ice_Sculpture4 = 32079;
	private static int Suspicious_Man = 32019;
	// Quest Items
	private static int Misas_Letter = 8079;
	private static int Raffortys_Letter = 8080;
	private static int Piece_of_Tablet = 8081;
	private static int Report_Piece = 8082;

	public _115_TheOtherSideOfTruth()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Misa);
		addTalkId(Kierre);
		addTalkId(Ice_Sculpture1);
		addTalkId(Ice_Sculpture2);
		addTalkId(Ice_Sculpture3);
		addTalkId(Ice_Sculpture4);
		addQuestItem(Misas_Letter);
		addQuestItem(Raffortys_Letter);
		addQuestItem(Piece_of_Tablet);
		addQuestItem(Report_Piece);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int _state = st.getState();
		if(event.equalsIgnoreCase("32020-02.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		if(_state != STARTED)
			return event;

		if(event.equalsIgnoreCase("32020-06.htm") || event.equalsIgnoreCase("32020-08a.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("32020-05.htm"))
		{
			st.set("cond", "3");
			st.takeItems(Misas_Letter, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32020-08.htm") || event.equalsIgnoreCase("32020-07a.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32020-12.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32018-04.htm"))
		{
			st.set("cond", "7");
			st.takeItems(Raffortys_Letter, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("Sculpture-04a.htm"))
		{
			st.set("cond", "8");
			st.playSound(SOUND_MIDDLE);
			if(st.getInt("32021") == 0 && st.getInt("32077") == 0)
				st.giveItems(Piece_of_Tablet, 1);

			Functions.npcSay(st.addSpawn(Suspicious_Man, 117890, -126478, -2584, 0, 0, 300000), "This looks like the right place...");

			return "Sculpture-04.htm";
		}
		else if(event.equalsIgnoreCase("32022-02.htm"))
		{
			st.set("cond", "9");
			st.giveItems(Report_Piece, 1);
			st.playSound(SOUND_MIDDLE);

			Functions.npcSay(st.addSpawn(Suspicious_Man, 104562, -107598, -3688, 0, 0, 300000), "We meet again.");
		}
		else if(event.equalsIgnoreCase("32020-16.htm"))
		{
			st.set("cond", "10");
			st.takeItems(Report_Piece, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32020-18.htm"))
		{
			if(st.getQuestItemsCount(Piece_of_Tablet) > 0)
			{
				st.giveItems(ADENA_ID, 60044);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				st.set("cond", "11");
				st.playSound(SOUND_MIDDLE);
				return "32020-19.htm";
			}
		}
		else if(event.equalsIgnoreCase("32020-19.htm"))
		{
			st.set("cond", "11");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.startsWith("32021") || event.startsWith("32077"))
		{
			if(event.contains("-pick"))
			{
				st.set("talk", "1");
				event = event.replace("-pick", "");
			}
			st.set(event, "1");
			return "Sculpture-05.htm";
		}

		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		if(_state == COMPLETED)
			return "completed";
		int npcId = npc.getNpcId();
		if(_state == CREATED)
		{
			if(npcId != Rafforty)
				return "noquest";
			if(st.getPlayer().getLevel() >= 53)
			{
				st.set("cond", "0");
				return "32020-01.htm";
			}
			st.exitCurrentQuest(true);
			return "32020-00.htm";
		}

		int cond = st.getInt("cond");
		if(npcId == Rafforty && _state == STARTED)
		{
			if(cond == 1)
				return "32020-03.htm";
			else if(cond == 2)
				return "32020-04.htm";
			else if(cond == 3)
				return "32020-05.htm";
			else if(cond == 4)
				return "32020-11.htm";
			else if(cond == 5)
			{
				st.set("cond", "6");
				st.giveItems(Raffortys_Letter, 1);
				st.playSound(SOUND_MIDDLE);
				return "32020-13.htm";
			}
			else if(cond == 6)
				return "32020-14.htm";
			else if(cond == 9)
				return "32020-15.htm";
			else if(cond == 10)
				return "32020-17.htm";
			else if(cond == 11)
				return "32020-20.htm";
			else if(cond == 12)
			{
				st.giveItems(ADENA_ID, 115673);
				st.addExpAndSp(493595, 40442);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
				return "32020-18.htm";
			}
		}
		else if(npcId == Misa && _state == STARTED)
		{
			if(cond == 1)
			{
				st.set("cond", "2");
				st.giveItems(Misas_Letter, 1);
				st.playSound(SOUND_MIDDLE);
				return "32018-01.htm";
			}
			else if(cond == 2)
				return "32018-02.htm";
			else if(cond == 6)
				return "32018-03.htm";
			else if(cond == 7)
				return "32018-05.htm";
		}
		else if(npcId == Kierre && _state == STARTED)
		{
			if(cond == 8)
				return "32022-01.htm";
			else if(cond == 9)
				return "32022-03.htm";
		}
		else if((npcId == Ice_Sculpture1 || npcId == Ice_Sculpture2 || npcId == Ice_Sculpture3 || npcId == Ice_Sculpture4) && _state == STARTED)
			if(cond == 7)
			{
				String _npcId = String.valueOf(npcId);
				int npcId_flag = st.getInt(_npcId);
				if(npcId == Ice_Sculpture1 || npcId == Ice_Sculpture2)
				{
					int talk_flag = st.getInt("talk");
					return npcId_flag == 1 ? "Sculpture-02.htm" : talk_flag == 1 ? "Sculpture-06.htm" : "Sculpture-03-" + _npcId + ".htm";
				}
				else if(npcId_flag == 1)
					return "Sculpture-02.htm";
				else
				{
					st.set(_npcId, "1");
					return "Sculpture-01.htm";
				}
			}
			else if(cond == 8)
				return "Sculpture-04.htm";
			else if(cond == 11)
			{
				st.set("cond", "12");
				st.giveItems(Piece_of_Tablet, 1);
				st.playSound(SOUND_MIDDLE);
				return "Sculpture-07.htm";
			}
			else if(cond == 12)
				return "Sculpture-08.htm";

		return "noquest";
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}