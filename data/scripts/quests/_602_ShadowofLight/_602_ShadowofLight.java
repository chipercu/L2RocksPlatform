package quests._602_ShadowofLight;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * Квест Shadowof Light
 * @author Sergey Ibryaev aka Artful
 */

public class _602_ShadowofLight extends Quest implements ScriptFile
{
	//NPC
	private static final int ARGOS = 31683;
	//Quest Item
	private static final int EYE_OF_DARKNESS = 7189;
	//Bonus
	private static final int[][] REWARDS = 
	{
		{ 6699, 40000, 120000, 20000, 1, 19 },
		{ 6698, 60000, 110000, 15000, 20, 39 },
		{ 6700, 40000, 150000, 10000, 40, 49 },
		{ 0, 100000, 140000, 11250, 50, 100 }
	};

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _602_ShadowofLight()
	{
		super(true);

		addStartNpc(ARGOS);

		addKillId(21299);
		addKillId(21304);

		addQuestItem(EYE_OF_DARKNESS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("eye_of_argos_q0602_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("eye_of_argos_q0602_0201.htm"))
		{
			st.takeItems(EYE_OF_DARKNESS, -1);
			int random = Rnd.get(100) + 1;
			for(int i = 0; i < REWARDS.length; i++)
				if(REWARDS[i][4] <= random && random <= REWARDS[i][5])
				{
					st.giveItems(ADENA_ID, REWARDS[i][1], true);
					st.addExpAndSp(REWARDS[i][2], REWARDS[i][3]);
					if(REWARDS[i][0] != 0)
						st.giveItems(REWARDS[i][0], 3, true);
				}
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if(id != CREATED)
			cond = st.getInt("cond");
		if(npcId == ARGOS)
			if(cond == 0)
				if(st.getPlayer().getLevel() < 68)
				{
					htmltext = "eye_of_argos_q0602_0103.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "eye_of_argos_q0602_0101.htm";
			else if(cond == 1)
				htmltext = "eye_of_argos_q0602_0106.htm";
			else if(cond == 2 && st.getQuestItemsCount(EYE_OF_DARKNESS) == 100)
				htmltext = "eye_of_argos_q0602_0105.htm";
			else
			{
				htmltext = "eye_of_argos_q0602_0106.htm";
				st.set("cond", "1");
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1)
		{
			long count = st.getQuestItemsCount(EYE_OF_DARKNESS);
			if(count < 100 && Rnd.chance(npc.getNpcId() == 21299 ? 35 : 40))
			{
				st.giveItems(EYE_OF_DARKNESS, 1);
				if(count == 99)
				{
					st.set("cond", "2");
					st.playSound(SOUND_MIDDLE);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}
