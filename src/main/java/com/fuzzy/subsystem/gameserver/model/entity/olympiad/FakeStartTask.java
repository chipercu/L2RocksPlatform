package com.fuzzy.subsystem.gameserver.model.entity.olympiad;

class FakeStartTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		Olympiad._manager = new OlympiadManager();
		//Olympiad._inCompPeriod = true;

		new Thread(Olympiad._manager).start();

		//ThreadPoolManager.getInstance().schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

		//Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
		Olympiad._log.info("Olympiad System: Fake Olympiad Game Started");
	}
}