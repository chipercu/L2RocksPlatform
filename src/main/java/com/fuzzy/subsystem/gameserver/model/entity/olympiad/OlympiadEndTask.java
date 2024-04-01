package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.Announcements;
import com.fuzzy.subsystem.gameserver.model.entity.Hero;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

public class OlympiadEndTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		if(Olympiad._scheduledOlympiadEnd != null)
			Olympiad._scheduledOlympiadEnd.cancel(true);

		if(Olympiad._inCompPeriod) // Если бои еще не закончились, откладываем окончание олимпиады на минуту
		{
			Olympiad._scheduledOlympiadEnd = ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), 60000);
			return;
		}

		Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_ENDED).addNumber(Olympiad._currentCycle));
		Announcements.getInstance().announceToAll("Olympiad Validation Period has began");

		Olympiad._isOlympiadEnd = true;
		if(Olympiad._scheduledManagerTask != null)
			Olympiad._scheduledManagerTask.cancel(true);
		if(Olympiad._scheduledWeeklyTask != null)
			Olympiad._scheduledWeeklyTask.cancel(true);

		Olympiad._validationEnd = Olympiad._olympiadEnd + ConfigValue.AltOlyVPeriod;

		OlympiadDatabase.saveNobleData();
		Olympiad._period = 1;
		Hero.getInstance().clearHeroes();

		try
		{
			OlympiadDatabase.save();
		}
		catch(Exception e)
		{
			Olympiad._log.warning("Olympiad System: Failed to save Olympiad configuration: " + e);
		}

		Olympiad._log.warning("Olympiad System: Starting Validation period. Time to end validation:" + Olympiad.getMillisToValidationEnd() / (60 * 1000));

		if(Olympiad._scheduledValdationTask != null)
			Olympiad._scheduledValdationTask.cancel(true);
		Olympiad._scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), Olympiad.getMillisToValidationEnd());
	}
}