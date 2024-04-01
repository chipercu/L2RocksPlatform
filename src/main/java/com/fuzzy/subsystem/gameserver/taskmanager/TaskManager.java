package com.fuzzy.subsystem.gameserver.taskmanager;

import javolution.util.FastMap;
import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.database.DatabaseUtils;
import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.gameserver.taskmanager.tasks.*;
import com.fuzzy.subsystem.util.GArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static com.fuzzy.subsystem.gameserver.taskmanager.TaskTypes.*;

public final class TaskManager
{
	static final Logger _log = Logger.getLogger(TaskManager.class.getName());
	private static TaskManager _instance;

	static final String[] SQL_STATEMENTS = {
			"SELECT id,task,type,last_activation,param1,param2,param3 FROM global_tasks",
			"UPDATE global_tasks SET last_activation=? WHERE id=?", "SELECT id FROM global_tasks WHERE task=?",
			"INSERT INTO global_tasks (task,type,last_activation,param1,param2,param3) VALUES(?,?,?,?,?,?)" };

	private final FastMap<Integer, Task> _tasks = new FastMap<Integer, Task>().setShared(true);
	final GArray<ExecutedTask> _currentTasks = new GArray<ExecutedTask>();

	public class ExecutedTask extends com.fuzzy.subsystem.common.RunnableImpl
	{
		int _id;
		long _lastActivation;
		Task _task;
		TaskTypes _type;
		String[] _params;
		ScheduledFuture<?> _scheduled;

		public ExecutedTask(Task task, TaskTypes type, ResultSet rset) throws SQLException
		{
			_task = task;
			_type = type;
			_id = rset.getInt("id");
			_lastActivation = rset.getLong("last_activation") * 1000L;
			_params = new String[] { rset.getString("param1"), rset.getString("param2"), rset.getString("param3") };
		}

		public void runImpl()
		{
			_task.onTimeElapsed(this);

			_lastActivation = System.currentTimeMillis();

			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(SQL_STATEMENTS[1]);
				statement.setLong(1, _lastActivation / 1000);
				statement.setInt(2, _id);
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				_log.warning("cannot updated the Global Task " + _id + ": " + e.getMessage());
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			if(_type == TYPE_SHEDULED || _type == TYPE_TIME)
				stopTask();
		}

		@Override
		public boolean equals(Object object)
		{
			return _id == ((ExecutedTask) object)._id;
		}

		public Task getTask()
		{
			return _task;
		}

		public TaskTypes getType()
		{
			return _type;
		}

		public int getId()
		{
			return _id;
		}

		public String[] getParams()
		{
			return _params;
		}

		public long getLastActivation()
		{
			return _lastActivation;
		}

		public void stopTask()
		{
			_task.onDestroy();

			if(_scheduled != null)
				_scheduled.cancel(true);

			_currentTasks.remove(this);
		}

	}

	public static TaskManager getInstance()
	{
		if(_instance == null)
			_instance = new TaskManager();
		return _instance;
	}

	public TaskManager()
	{
		initializate();
		startAllTasks();
	}

	private void initializate()
	{
		registerTask(new TaskRecom());
		if(ConfigValue.EnableOlympiad)
			registerTask(new TaskOlympiadSave());
		if(ConfigValue.EnableClanPoint)
			registerTask(new TaskClanPoint());
		registerTask(new TaskNavit());
	}

	public void registerTask(Task task)
	{
		int key = task.getName().hashCode();
		if(!_tasks.containsKey(key))
		{
			_tasks.put(key, task);
			task.initializate();
		}
	}

	private void startAllTasks()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SQL_STATEMENTS[0]);
			rset = statement.executeQuery();

			while(rset.next())
			{
				Task task = _tasks.get(rset.getString("task").trim().toLowerCase().hashCode());

				if(task == null)
					continue;

				TaskTypes type = TaskTypes.valueOf(rset.getString("type"));

				if(type != TYPE_NONE)
				{
					ExecutedTask current = new ExecutedTask(task, type, rset);
					if(launchTask(current))
						_currentTasks.add(current);
				}

			}
		}
		catch(Exception e)
		{
			_log.severe("error while loading Global Task table " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private boolean launchTask(ExecutedTask task)
	{
		final ThreadPoolManager scheduler = ThreadPoolManager.getInstance();
		final TaskTypes type = task.getType();

		if(type == TYPE_STARTUP)
		{
			task.run();
			return false;
		}
		else if(type == TYPE_SHEDULED)
		{
			long delay = Long.valueOf(task.getParams()[0]);
			task._scheduled = scheduler.schedule(task, delay);
			return true;
		}
		else if(type == TYPE_FIXED_SHEDULED)
		{
			long delay = Long.valueOf(task.getParams()[0]);
			long interval = Long.valueOf(task.getParams()[1]);

			task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);
			return true;
		}
		else if(type == TYPE_TIME)
			try
			{
				Date desired = DateFormat.getInstance().parse(task.getParams()[0]);
				long diff = desired.getTime() - System.currentTimeMillis();
				if(diff >= 0)
				{
					task._scheduled = scheduler.schedule(task, diff);
					return true;
				}
				_log.info("Task " + task.getId() + " is obsoleted.");
			}
			catch(Exception e)
			{}
		else if(type == TYPE_SPECIAL)
		{
			ScheduledFuture<?> result = task.getTask().launchSpecial(task);
			if(result != null)
			{
				task._scheduled = result;
				return true;
			}
		}
		else if(type == TYPE_GLOBAL_TASK)
		{
			long interval = Long.valueOf(task.getParams()[0]) * 86400000L;
			String[] hour = task.getParams()[1].split(":");

			if(hour.length != 3)
			{
				_log.warning("Task " + task.getId() + " has incorrect parameters");
				return false;
			}

			Calendar check = Calendar.getInstance();
			check.setTimeInMillis(task.getLastActivation() + interval);

			Calendar min = Calendar.getInstance();
			try
			{
				min.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour[0]));
				min.set(Calendar.MINUTE, Integer.valueOf(hour[1]));
				min.set(Calendar.SECOND, Integer.valueOf(hour[2]));
			}
			catch(Exception e)
			{
				_log.warning("Bad parameter on task " + task.getId() + ": " + e.getMessage());
				return false;
			}

			long delay = min.getTimeInMillis() - System.currentTimeMillis();

			if(check.after(min) || delay < 0)
				delay += interval;

			task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);

			return true;
		}
		else if(type == TYPE_EVERY_HOUR)
		{
			long interval = 3600000L;
			Calendar check = Calendar.getInstance();
			check.setTimeInMillis(task.getLastActivation() + interval);

			Calendar min = Calendar.getInstance();
			min.set(Calendar.MINUTE, 0);
			min.set(Calendar.SECOND, 0);

			long delay = min.getTimeInMillis() - System.currentTimeMillis();

			if(check.after(min) || delay < 0)
				delay += interval;

			task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);
			return true;
		}

		return false;
	}

	public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addUniqueTask(task, type, param1, param2, param3, 0);
	}

	public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SQL_STATEMENTS[2]);
			statement.setString(1, task);
			rset = statement.executeQuery();
			boolean exists = rset.next();
			DatabaseUtils.closeDatabaseSR(statement, rset);
			if(!exists)
			{
				statement = con.prepareStatement(SQL_STATEMENTS[3]);
				statement.setString(1, task);
				statement.setString(2, type.toString());
				statement.setLong(3, lastActivation / 1000);
				statement.setString(4, param1);
				statement.setString(5, param2);
				statement.setString(6, param3);
				statement.execute();
			}
			return true;
		}
		catch(SQLException e)
		{
			_log.warning("cannot add the unique task: " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return false;
	}

	public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addTask(task, type, param1, param2, param3, 0);
	}

	public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SQL_STATEMENTS[3]);
			statement.setString(1, task);
			statement.setString(2, type.toString());
			statement.setLong(3, lastActivation / 1000);
			statement.setString(4, param1);
			statement.setString(5, param2);
			statement.setString(6, param3);
			statement.execute();
			return true;
		}
		catch(SQLException e)
		{
			_log.warning("cannot add the task:	" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		return false;
	}
}