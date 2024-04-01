package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.base.Experience;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.serverpackets.RadarControl;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class L2NewbieGuideInstance extends L2NpcInstance
{
	private static Logger _log = Logger.getLogger(L2NewbieGuideInstance.class.getName());
	private static final List<?> mainHelpers = Arrays.asList(30598, 30599, 30600, 30601, 30602, 32135);

	public L2NewbieGuideInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(val == 0 && mainHelpers.contains(getNpcId()))
			if(player.getClassId().getLevel() == 1)
			{
				if(player.getLevel() == 1)
					player.addExpAndSp(Experience.LEVEL[2] - player.getExp(), 50, false, false);
				if(player.getLevel() < 6) // FIXME: если получить 6 левел во время квеста то награду не дадут
					if(player.isQuestCompleted("_001_LettersOfLove") || player.isQuestCompleted("_002_WhatWomenWant") || player.isQuestCompleted("_004_LongLivethePaagrioLord") || player.isQuestCompleted("_005_MinersFavor") || player.isQuestCompleted("_166_DarkMass") || player.isQuestCompleted("_174_SupplyCheck"))
					{
						if(!player.getVarB("ng1"))
						{
							String oldVar = player.getVar("ng1");
							player.setVar("ng1", oldVar == null ? "1" : String.valueOf(Integer.parseInt(oldVar) + 1));
							Functions.addItem(player, 57, 11567);
							player.addExpAndSp(Experience.LEVEL[6] - player.getExp(), 127, false, false);
						}
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q1-2.htm", val));
						return;
					}
					else
					{
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q1-1.htm", val).replace("%tonpc%", getQuestNpc(1, player)));
						return;
					}
				if(player.getLevel() < 10)
					if(player.getVarB("p1q2"))
					{
						if(!player.getVarB("ng2"))
						{
							// TODO: адена?
							String oldVar = player.getVar("ng2");
							player.setVar("ng2", oldVar == null ? "1" : String.valueOf(Integer.parseInt(oldVar) + 1));
							long addexp = Experience.LEVEL[10] - player.getExp();
							player.addExpAndSp(addexp, addexp / 24, false, false);
						}
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q3-1.htm", val).replace("%tonpc%", getQuestNpc(3, player)));
						return;
					}
					else
					{
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q2-1.htm", val).replace("%tonpc%", getQuestNpc(2, player)));
						return;
					}
				if(player.getLevel() < 15)
					if(player.getVarB("p1q3"))
					{
						if(!player.getVarB("ng3"))
						{
							String oldVar = player.getVar("ng3");
							player.setVar("ng3", oldVar == null ? "1" : String.valueOf(Integer.parseInt(oldVar) + 1));
							Functions.addItem(player, 57, 38180);
							long addexp = Experience.LEVEL[15] - player.getExp();
							player.addExpAndSp(addexp, addexp / 22, false, false);
						}
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q4-1.htm", val).replace("%tonpc%", getQuestNpc(4, player)));
						return;
					}
					else
					{
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q3-1.htm", val).replace("%tonpc%", getQuestNpc(3, player)));
						return;
					}
				if(player.getLevel() < 18)
					if(player.getVarB("p1q4"))
					{
						if(!player.getVarB("ng4"))
						{
							String oldVar = player.getVar("ng4");
							player.setVar("ng4", oldVar == null ? "1" : String.valueOf(Integer.parseInt(oldVar) + 1));
							Functions.addItem(player, 57, 10018);
							long addexp = Experience.LEVEL[18] - player.getExp();
							player.addExpAndSp(addexp, addexp / 5, false, false);
						}
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q4-2.htm", val));
						return;
					}
					else
					{
						player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q4-1.htm", val).replace("%tonpc%", getQuestNpc(4, player)));
						return;
					}

				player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q-no.htm", val));
				return;
			}
			else
			{
				player.sendPacket(new NpcHtmlMessage(player, this, "data/html/newbiehelper/q-no.htm", val));
				return;
			}
		super.showChatWindow(player, val);
	}

	public String getQuestNpc(int quest, L2Player player)
	{
		int val = 0;
		switch(quest)
		{
			case 1: // level 2
				switch(getNpcId())
				{
					case 30598: // Human
						val = 30048; // Darin, _001_LettersOfLove
						break;
					case 30599: // Elf
						val = 30223; // Arujien, _002_WhatWomenWant
						break;
					case 30600: // Dark Elf
						val = 30130; // Undrias, _166_DarkMass
						break;
					case 30601: // Dwarf
						val = 30554; // Bolter, _005_MinersFavor
						break;
					case 30602: // Orc
						val = 30578; // Nakusin, _004_LongLivethePaagrioLord
						break;
					case 32135: // Kamael
						val = 32173; // Marcela, _174_SupplyCheck
						break;
				}
				break;
			case 2: // level 6
				switch(getNpcId())
				{
					case 30598: // Human
						val = 30039; // Gilbert, _257_GuardIsBusy
						break;
					case 30599: // Elf
						val = 30221; // Rayen, _260_HuntTheOrcs
						break;
					case 30600: // Dark Elf
						val = 30357; // Kristin, _265_ChainsOfSlavery
						break;
					case 30601: // Dwarf
						val = 30535; // Filaur, _293_HiddenVein
						break;
					case 30602: // Orc
						val = 30566; // Varkees, _273_InvadersOfHolyland
						break;
					case 32135: // Kamael
						val = 32173; // Marcela, _281_HeadForTheHills
						break;
				}
				break;
			case 3: // level 10
				switch(player.getClassId())
				{
					case fighter:
						val = 30008; // Roien, _101_SwordOfSolidarity
						break;
					case mage:
						val = 30017; // Gallint, _104_SpiritOfMirror
						break;
					case elvenFighter:
					case elvenMage:
						val = 30218; // Kendell, _105_SkirmishWithOrcs
						break;
					case darkFighter:
					case darkMage:
						val = 30358; // Thifiell, _106_ForgottenTruth
						break;
					case orcFighter:
					case orcMage:
						val = 30568; // Hatos, _107_MercilessPunishment
						break;
					case dwarvenFighter:
						val = 30523; // Gouph, _108_JumbleTumbleDiamondFuss
						break;
					case maleSoldier:
					case femaleSoldier:
						val = 32138; // Kekropus, _175_TheWayOfTheWarrior
						break;
				}
				break;
			case 4: // level 15
				switch(getNpcId())
				{
					case 30598: // Human
						val = 30050; // Elias, _151_CureforFeverDisease
						break;
					case 30599: // Elf
						val = 30222; // Alshupes, _261_CollectorsDream
						break;
					case 30600: // Dark Elf
						val = 30145; // Vlasty, _169_OffspringOfNightmares
						break;
					case 30601: // Dwarf
						val = 30519; // Mion, _296_SilkOfTarantula
						break;
					case 30602: // Orc
						val = 30571; // Tanapi, _276_HestuiTotem
						break;
					case 32135: // Kamael
						val = 32133; // Perwan, _283_TheFewTheProudTheBrave
						break;
				}
				break;
		}

		if(val == 0)
		{
			_log.warning("WTF? L2NewbieGuideInstance " + getNpcId() + " not found next step " + quest + " for " + player.getClassId());
			return null;
		}

		L2NpcInstance npc = L2ObjectsStorage.getByNpcId(val);
		if(npc == null)
			return "";

		player.sendPacket(new RadarControl(2, 1, npc.getLoc()));// Убираем флажок на карте и стрелку на компасе
		player.sendPacket(new RadarControl(0, 2, npc.getLoc()));// Ставим флажок на карте и стрелку на компасе

		return npc.getName();
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		String temp = "data/html/newbiehelper/" + pom + ".htm";

		File mainText = new File(temp);

		// Return the pathfile of the HTML file
		if(mainText.exists())
			return temp;

		// if the file is not found, the standard message "I have nothing to say to you" is returned
		return super.getHtmlPath(npcId, val);
	}
}
