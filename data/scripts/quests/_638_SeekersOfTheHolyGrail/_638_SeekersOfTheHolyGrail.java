package quests._638_SeekersOfTheHolyGrail;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _638_SeekersOfTheHolyGrail extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int DROP_CHANCE = 10; // Для х1 мобов
	private static final int INNOCENTIN = 31328;
	private static final int TOTEM = 8068;
	private static final int EAS = 960;
	private static final int EWS = 959;

	public _638_SeekersOfTheHolyGrail()
	{
		super(true);
		addStartNpc(INNOCENTIN);
		addQuestItem(TOTEM);
		for(int i = 22137; i <= 22176; i++)
			addKillId(i);
		addKillId(22194);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("highpriest_innocentin_q0638_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("highpriest_innocentin_q0638_09.htm"))
		{
			st.playSound(SOUND_GIVEUP);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();

		if(id == CREATED)
		{
			if(st.getPlayer().getLevel() >= 73)
				htmltext = "highpriest_innocentin_q0638_01.htm";
			else
				htmltext = "highpriest_innocentin_q0638_02.htm";
		}
		else
			htmltext = tryRevard(st);

		return htmltext;
	}

	private String tryRevard(QuestState st)
	{
		boolean ok = false;
		while(st.getQuestItemsCount(TOTEM) >= 2000)
		{
			st.takeItems(TOTEM, 2000);
			int rnd = Rnd.get(100);
			if(rnd < 50)
				st.giveItems(ADENA_ID, 3576000, false);
			else if(rnd < 85)
				st.giveItems(EAS, 1, false);
			else
				st.giveItems(EWS, 1, false);
			ok = true;
		}
		if(ok)
		{
			st.playSound(SOUND_MIDDLE);
			return "highpriest_innocentin_q0638_10.htm";
		}
		return "highpriest_innocentin_q0638_05.htm";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(TOTEM, 1, DROP_CHANCE * npc.getTemplate().rateHp);
		return null;
	}
}