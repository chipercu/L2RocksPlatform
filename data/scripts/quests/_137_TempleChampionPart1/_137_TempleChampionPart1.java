package quests._137_TempleChampionPart1;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _137_TempleChampionPart1 extends Quest implements ScriptFile
{
	// NPCs
	private static final int SYLVAIN = 30070;

	// ITEMs
	private static final int FRAGMENT = 10340;
	private static final int BadgeTempleExecutor = 10334;
	private static final int BadgeTempleMissionary = 10339;

	// Monsters
	private final static int GraniteGolem = 20083;
	private final static int HangmanTree = 20144;
	private final static int AmberBasilisk = 20199;
	private final static int Strain = 20200;
	private final static int Ghoul = 20201;
	private final static int DeadSeeker = 20202;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _137_TempleChampionPart1()
	{
		super(false);

		addStartNpc(SYLVAIN);
		addKillId(GraniteGolem, HangmanTree, AmberBasilisk, Strain, Ghoul, DeadSeeker);
		addQuestItem(FRAGMENT);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("sylvain_q0137_04.htm"))
		{
			st.takeItems(BadgeTempleExecutor, -1);
			st.takeItems(BadgeTempleMissionary, -1);
			st.set("cond", "1");
			st.setState(STARTED);
			st.set("talk", "0");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("sylvain_q0137_08.htm"))
			st.set("talk", "1");
		else if(event.equalsIgnoreCase("sylvain_q0137_10.htm"))
			st.set("talk", "2");
		else if(event.equalsIgnoreCase("sylvain_q0137_13.htm"))
		{
			st.unset("talk");
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("sylvain_q0137_24.htm"))
		{
			st.giveItems(ADENA_ID, 69146);
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(219975, 13047);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == SYLVAIN)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 35 && st.getQuestItemsCount(BadgeTempleExecutor) > 0 && st.getQuestItemsCount(BadgeTempleMissionary) > 0)
					htmltext = "sylvain_q0137_01.htm";
				else
				{
					htmltext = "sylvain_q0137_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
			{
				if(st.getInt("talk") == 0)
					htmltext = "sylvain_q0137_05.htm";
				else if(st.getInt("talk") == 1)
					htmltext = "sylvain_q0137_08.htm";
				else if(st.getInt("talk") == 2)
					htmltext = "sylvain_q0137_10.htm";
			}
			else if(cond == 2)
				htmltext = "sylvain_q0137_13.htm";
			else if(cond == 3 && st.getQuestItemsCount(FRAGMENT) >= 30)
			{
				htmltext = "sylvain_q0137_15.htm";
				st.set("talk", "1");
				st.takeItems(FRAGMENT, -1);
			}
			else if(cond == 3 && st.getInt("talk") == 1)
				htmltext = "sylvain_q0137_16.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 2)
			if(st.getQuestItemsCount(FRAGMENT) < 30)
			{
				st.giveItems(FRAGMENT, 1);
				if(st.getQuestItemsCount(FRAGMENT) >= 30)
				{
					st.set("cond", "3");
					st.playSound(SOUND_MIDDLE);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}