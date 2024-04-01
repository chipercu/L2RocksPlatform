package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

import com.fuzzy.subsystem.gameserver.Announcements;
import com.fuzzy.subsystem.gameserver.instancemanager.OlympiadHistoryManager;
import com.fuzzy.subsystem.gameserver.model.entity.Hero;

public class ValidationTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		OlympiadHistoryManager.getInstance().switchData();
		OlympiadDatabase.sortHerosToBe();
		OlympiadDatabase.saveNobleData();
		if(Hero.getInstance().computeNewHeroes(Olympiad._heroesToBe))
			Olympiad._log.warning("Olympiad: Error while computing new heroes!");
		Olympiad._log.warning("Olympiad: Validation Period has ended.");
		Announcements.getInstance().announceToAll("Olympiad Validation Period has ended");
		Olympiad._period = 0;
		Olympiad._currentCycle++;
		OlympiadDatabase.cleanupNobles();
		OlympiadDatabase.loadNoblesRank();
		Olympiad.removeBattlesCount(); // ?
		OlympiadDatabase.setNewOlympiadEnd();
		Olympiad.init();
		OlympiadDatabase.save();
	}
}