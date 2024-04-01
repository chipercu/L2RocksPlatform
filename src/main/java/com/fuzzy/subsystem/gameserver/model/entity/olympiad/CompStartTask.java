package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.Announcements;
import com.fuzzy.subsystem.gameserver.cache.Msg;

class CompStartTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._manager = new OlympiadManager();
		Olympiad._inCompPeriod = true;

		new Thread(Olympiad._manager).start();

		ThreadPoolManager.getInstance().schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

		Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
		Olympiad._log.info("Olympiad System: Olympiad Game Started");
		if(ConfigValue.OlympiadStatEnable)
			OlympiadStat.update_week_stat();
	}
}