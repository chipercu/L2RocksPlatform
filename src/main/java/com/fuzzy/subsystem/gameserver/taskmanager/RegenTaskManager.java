package com.fuzzy.subsystem.gameserver.taskmanager;

import com.fuzzy.subsystem.common.*;

public class RegenTaskManager extends SteppingRunnable
{
	private static final RegenTaskManager _instance = new RegenTaskManager();
	public static final RegenTaskManager getInstance()
	{
		return _instance;
	}

	private RegenTaskManager()
	{
		super(333L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 333L, 333L);
		//Очистка каждые 10 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new com.fuzzy.subsystem.common.RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				RegenTaskManager.this.purge();
			}
		}, 10000L, 10000L);
	}
}