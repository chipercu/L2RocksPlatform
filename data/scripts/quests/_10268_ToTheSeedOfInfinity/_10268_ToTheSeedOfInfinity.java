package quests._10268_ToTheSeedOfInfinity;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10268_ToTheSeedOfInfinity extends Quest implements ScriptFile
{
	private final static int Keucereus = 32548;
	private final static int Tepios = 32603;

	private final static int Introduction = 13811;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _10268_ToTheSeedOfInfinity()
	{
		super(false);

		addStartNpc(Keucereus);
		addTalkId(Tepios);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32548-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Introduction, 1);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int npcId = npc.getNpcId();
		switch(id)
		{
			case CREATED:
				if(npcId == Keucereus)
					if(st.getPlayer().getLevel() < 75)
						htmltext = "32548-00.htm";
					else
						htmltext = "32548-01.htm";
				break;
			case STARTED:
				if(npcId == Keucereus)
					htmltext = "32548-06.htm";
				else if(npcId == Tepios)
				{
					htmltext = "32530-01.htm";
					st.giveItems(ADENA_ID, 16671);
					st.addExpAndSp(100640, 10098, true);
					st.exitCurrentQuest(false);
					st.playSound(SOUND_FINISH);
				}
				break;
		}
		return htmltext;
	}
}