package com.fuzzy.subsystem.gameserver.taskmanager.tasks;

import com.fuzzy.subsystem.database.mysql;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.taskmanager.Task;
import com.fuzzy.subsystem.gameserver.taskmanager.TaskManager;
import com.fuzzy.subsystem.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.fuzzy.subsystem.gameserver.taskmanager.TaskTypes;

import java.util.logging.Logger;

public class TaskRecom extends Task
{
	private static final Logger _log = Logger.getLogger(TaskRecom.class.getName());
	private static final String NAME = "sp_recommendations";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		_log.info("Recommendation Global Task: launched.");
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.getRecommendation().restartRecom();
		mysql.set("UPDATE `characters` SET `rec_timeleft`=3600");
		_log.info("Recommendation Global Task: completed.");
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "6:30:00", "");
	}
}