package com.fuzzy.subsystem.gameserver.taskmanager.tasks;

import com.fuzzy.subsystem.database.mysql;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.taskmanager.Task;
import com.fuzzy.subsystem.gameserver.taskmanager.TaskManager;
import com.fuzzy.subsystem.gameserver.taskmanager.TaskTypes;

import java.util.logging.Logger;

public class TaskNavit extends Task
{
	private static final Logger _log = Logger.getLogger(TaskNavit.class.getName());
	private static final String NAME = "clear_nevit";

	public String getName()
	{
		return NAME;
	}

	public void onTimeElapsed(TaskManager.ExecutedTask task)
	{
		_log.info("Navit Global Task: launched.");
		for (L2Player player : L2ObjectsStorage.getPlayers())
			player.getNevitBlessing().setBonusTime(0);
		mysql.set("UPDATE `characters` SET `hunt_bonus`=0");

		_log.info("Navit Global Task: completed.");
	}

	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "6:00:00", "");
	}
}