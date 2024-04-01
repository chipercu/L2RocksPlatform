package quests._049_TheRoadHome;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _049_TheRoadHome extends Quest implements ScriptFile
{
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_1 = 7563;
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_2 = 7564;
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_3 = 7565;
	private static final int MAGIC_SWORD_HILT_ID = 7568;
	private static final int GEMSTONE_POWDER_ID = 7567;
	private static final int PURIFIED_MAGIC_NECKLACE_ID = 7566;
	private static final int MARK_OF_TRAVELER_ID = 7570;
	private static final int SCROLL_OF_ESCAPE_DWARVEN_VILLAGE = 7121;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _049_TheRoadHome()
	{
		super(false);

		addStartNpc(30097);

		addTalkId(30097);
		addTalkId(30094);
		addTalkId(30090);
		addTalkId(30116);

		addQuestItem(new int[] { GALLADUCCIS_ORDER_DOCUMENT_ID_1, GALLADUCCIS_ORDER_DOCUMENT_ID_2,
				GALLADUCCIS_ORDER_DOCUMENT_ID_3, MAGIC_SWORD_HILT_ID, GEMSTONE_POWDER_ID, PURIFIED_MAGIC_NECKLACE_ID });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1, 1);
			htmltext = "galladuchi_q0049_0104.htm";
		}
		else if(event.equals("2"))
		{
			st.set("cond", "2");
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1, 1);
			st.giveItems(MAGIC_SWORD_HILT_ID, 1);
			htmltext = "gentler_q0049_0201.htm";
		}
		else if(event.equals("3"))
		{
			st.set("cond", "3");
			st.takeItems(MAGIC_SWORD_HILT_ID, 1);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2, 1);
			htmltext = "galladuchi_q0049_0301.htm";
		}
		else if(event.equals("4"))
		{
			st.set("cond", "4");
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2, 1);
			st.giveItems(GEMSTONE_POWDER_ID, 1);
			htmltext = "sandra_q0049_0401.htm";
		}
		else if(event.equals("5"))
		{
			st.set("cond", "5");
			st.takeItems(GEMSTONE_POWDER_ID, 1);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3, 1);
			htmltext = "galladuchi_q0049_0501.htm";
		}
		else if(event.equals("6"))
		{
			st.set("cond", "6");
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3, 1);
			st.giveItems(PURIFIED_MAGIC_NECKLACE_ID, 1);
			htmltext = "dustin_q0049_0601.htm";
		}
		else if(event.equals("7"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_DWARVEN_VILLAGE, 1);
			st.takeItems(PURIFIED_MAGIC_NECKLACE_ID, 1);
			htmltext = "galladuchi_q0049_0701.htm";
			st.set("cond", "0");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
		{
			if(st.getPlayer().getRace() != Race.dwarf || st.getQuestItemsCount(MARK_OF_TRAVELER_ID) == 0)
			{
				htmltext = "galladuchi_q0049_0102.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() < 3)
			{
				htmltext = "galladuchi_q0049_0103.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "galladuchi_q0049_0101.htm";
		}
		else if(npcId == 30097 && st.getInt("cond") == 1)
			htmltext = "galladuchi_q0049_0105.htm";
		else if(npcId == 30097 && st.getInt("cond") == 2)
			htmltext = "galladuchi_q0049_0201.htm";
		else if(npcId == 30097 && st.getInt("cond") == 3)
			htmltext = "galladuchi_q0049_0303.htm";
		else if(npcId == 30097 && st.getInt("cond") == 4)
			htmltext = "galladuchi_q0049_0401.htm";
		else if(npcId == 30097 && st.getInt("cond") == 5)
			htmltext = "galladuchi_q0049_0503.htm";
		else if(npcId == 30097 && st.getInt("cond") == 6)
			htmltext = "galladuchi_q0049_0601.htm";
		else if(npcId == 30094 && st.getInt("cond") == 1)
			htmltext = "gentler_q0049_0101.htm";
		else if(npcId == 30094 && st.getInt("cond") == 2)
			htmltext = "gentler_q0049_0203.htm";
		else if(npcId == 30090 && st.getInt("cond") == 3)
			htmltext = "sandra_q0049_0301.htm";
		else if(npcId == 30090 && st.getInt("cond") == 4)
			htmltext = "sandra_q0049_0403.htm";
		else if(npcId == 30116 && st.getInt("cond") == 5)
			htmltext = "dustin_q0049_0501.htm";
		else if(npcId == 30116 && st.getInt("cond") == 6)
			htmltext = "dustin_q0049_0603.htm";
		return htmltext;
	}
}