package quests._130_PathToHellbound;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * User: Keiichi
 * Date: 05.10.2008
 * Time: 19:45:18
 * Info: Один из 2х квестов для прохода на остров Hellbound.
 * Info: Пройдя его ведьма Galate открывает ТП до локации (xyz = -11095, 236440, -3232)
 */
public class _130_PathToHellbound extends Quest implements ScriptFile
{
	// NPC's
	private static int CASIAN = 30612;
	private static int GALATE = 32292;
	// ITEMS
	private static int CASIAN_BLUE_CRY = 12823;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _130_PathToHellbound()
	{
		super(false);

		addStartNpc(CASIAN);
		addTalkId(GALATE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int cond = st.getInt("cond");
		String htmltext = event;

		if(event.equals("sage_kasian_q0130_05.htm") && cond == 0)
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		if(event.equals("galate_q0130_03.htm") && cond == 1)
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}

		if(event.equals("sage_kasian_q0130_08.htm") && cond == 2)
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(CASIAN_BLUE_CRY, 1);
		}

		if(event.equals("galate_q0130_07.htm") && cond == 3)
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(CASIAN_BLUE_CRY, -1);
			st.exitCurrentQuest(false);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");

		if(npcId == CASIAN)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 78)
					htmltext = "sage_kasian_q0130_01.htm";
				else
				{
					htmltext = "sage_kasian_q0130_02.htm";
					st.exitCurrentQuest(true);
				}
			if(cond == 2)
				htmltext = "sage_kasian_q0130_07.htm";
		}

		else if(id == STARTED)
			if(npcId == GALATE)
			{
				if(cond == 1)
					htmltext = "galate_q0130_01.htm";

				if(cond == 3)
					htmltext = "galate_q0130_05.htm";
			}

		return htmltext;
	}
}