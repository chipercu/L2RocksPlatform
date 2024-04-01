package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.Announcements;
import com.fuzzy.subsystem.gameserver.cache.Msg;

class CompEndTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._inCompPeriod = false;

		try
		{
			// Если остались игры, ждем их завершения еще одну минуту 
			if(Olympiad.getActiveGames() != null && !Olympiad.getActiveGames().isEmpty())
			{
				ThreadPoolManager.getInstance().schedule(new CompEndTask(), 60000);
				return;
			}

			Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
			Olympiad._log.info("Olympiad System: Olympiad Game Ended");
			OlympiadDatabase.save();
			if(ConfigValue.OlympiadStatEnable)
				OlympiadStat.set_day_reward();
		}
		catch(Exception e)
		{
			Olympiad._log.warning("Olympiad System: Failed to save Olympiad configuration:");
			e.printStackTrace();
		}
		Olympiad.init();
	}
}