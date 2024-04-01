package quests._406_PathToElvenKnight;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NewbieGuideInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Квест на профессию Path To Elven Knight
 * @author Sergey Ibryaev aka Artful
 */

public class _406_PathToElvenKnight extends Quest implements ScriptFile
{
	//NPC
	private static final int Sorius = 30327;
	private static final int Kluto = 30317;
	//QuestItems
	private static final int SoriussLetter = 1202;
	private static final int KlutoBox = 1203;
	private static final int TopazPiece = 1205;
	private static final int EmeraldPiece = 1206;
	private static final int KlutosMemo = 1276;
	//Items
	private static final int ElvenKnightBrooch = 1204;
	//MOB
	private static final int TrackerSkeleton = 20035;
	private static final int TrackerSkeletonLeader = 20042;
	private static final int SkeletonScout = 20045;
	private static final int SkeletonBowman = 20051;
	private static final int RagingSpartoi = 20060;
	private static final int OlMahumNovice = 20782;
	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]	
	private static final int[][] DROPLIST_COND = { { 1, 2, TrackerSkeleton, 0, TopazPiece, 20, 70, 1 },
			{ 1, 2, TrackerSkeletonLeader, 0, TopazPiece, 20, 70, 1 }, { 1, 2, SkeletonScout, 0, TopazPiece, 20, 70, 1 },
			{ 1, 2, SkeletonBowman, 0, TopazPiece, 20, 70, 1 }, { 1, 2, RagingSpartoi, 0, TopazPiece, 20, 70, 1 },
			{ 4, 5, OlMahumNovice, 0, EmeraldPiece, 20, 50, 1 } };

	private static boolean QuestProf = true;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _406_PathToElvenKnight()
	{
		super(false);

		addStartNpc(Sorius);
		addTalkId(Kluto);

		// Mob Drop
		for(int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][2]);

		addQuestItem(new int[] { TopazPiece, EmeraldPiece, SoriussLetter, KlutosMemo, KlutoBox });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("master_sorius_q0406_05.htm"))
		{
			if(st.getPlayer().getClassId().getId() == 0x12)
			{
				if(st.getQuestItemsCount(ElvenKnightBrooch) > 0)
				{
					htmltext = "master_sorius_q0406_04.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getLevel() < 18)
				{
					htmltext = "master_sorius_q0406_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(st.getPlayer().getClassId().getId() == 0x13)
			{
				htmltext = "master_sorius_q0406_02a.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "master_sorius_q0406_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("master_sorius_q0406_06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		else if(event.equalsIgnoreCase("blacksmith_kluto_q0406_02.htm"))
		{
			st.takeItems(SoriussLetter, -1);
			st.giveItems(KlutosMemo, 1);
			st.set("cond", "4");
			st.setState(STARTED);
		}
		else
			htmltext = "noquest";

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(npcId == Sorius)
		{
			if(cond == 0)
				htmltext = "master_sorius_q0406_01.htm";
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(TopazPiece) == 0)
					htmltext = "master_sorius_q0406_07.htm";
				else
					htmltext = "master_sorius_q0406_08.htm";
			}
			else if(cond == 2)
			{
				st.takeItems(TopazPiece, -1);
				st.giveItems(SoriussLetter, 1);
				htmltext = "master_sorius_q0406_09.htm";
				st.set("cond", "3");
				st.setState(STARTED);
			}
			else if(cond == 3 || cond == 4 || cond == 5)
				htmltext = "master_sorius_q0406_11.htm";
			else if(cond == 6)
			{
				st.takeItems(KlutoBox, -1);
				if(st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(ElvenKnightBrooch, 1);
					if(!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1");
						st.addExpAndSp(228064, 16455, true);
						st.giveItems(ADENA_ID, 81900, ConfigValue.RateQuestsRewardOccupationChange);
					}
				}
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
				htmltext = "master_sorius_q0406_10.htm";
			}
		}
		else if(npcId == Kluto)
			if(cond == 3)
				htmltext = "blacksmith_kluto_q0406_01.htm";
			else if(cond == 4)
			{
				if(st.getQuestItemsCount(EmeraldPiece) == 0)
					htmltext = "blacksmith_kluto_q0406_03.htm";
				else
					htmltext = "blacksmith_kluto_q0406_04.htm";
			}
			else if(cond == 5)
			{
				st.takeItems(EmeraldPiece, -1);
				st.takeItems(KlutosMemo, -1);
				st.giveItems(KlutoBox, 1);
				htmltext = "blacksmith_kluto_q0406_05.htm";
				st.set("cond", "6");
				st.setState(STARTED);
			}
			else if(cond == 6)
				htmltext = "blacksmith_kluto_q0406_06.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		for(int i = 0; i < DROPLIST_COND.length; i++)
			if(cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
				if(DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
					if(DROPLIST_COND[i][5] == 0)
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6], QuestProf);
					else if(st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6], QuestProf))
						if(DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.set("cond", String.valueOf(DROPLIST_COND[i][1]));
							st.setState(STARTED);
						}
		return null;
	}
}
