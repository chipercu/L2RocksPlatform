package com.fuzzy.subsystem.gameserver.tables;

import com.fuzzy.subsystem.config.*;
import com.fuzzy.subsystem.database.*;
import com.fuzzy.subsystem.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerCombine
{
	private static Logger _log = Logger.getLogger(SkillTreeTable.class.getName());
	private static int max_id = 0;
	private static int start_id = 268435456;
	private static int diff_id = 10000000;
	// 31284181
	// 41284181
	public static void main(String[] args) throws Exception
	{
		InputStream is = new FileInputStream(new File("./config/log.properties"));
		LogManager.getLogManager().readConfiguration(is);
		is.close();

        ConfigSystem.load();

		L2DatabaseFactory.getInstance();
		Log.InitGSLoggers();

		load_max_id();
	}

	private static void load_max_id()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT Max(obj_Id) FROM characters UNION SELECT Max(object_id) FROM items UNION SELECT Max(clan_id) FROM clan_data UNION SELECT Max(ally_id) FROM ally_data UNION SELECT Max(objId) FROM pets UNION SELECT Max(id) FROM couples");
			rs = statement.executeQuery();

			while(rs.next())
				max_id = Math.max(max_id, rs.getInt(1));
			diff_id += max_id-start_id;
			_log.info("Max id: "+max_id+" diff id: "+diff_id);
			
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "error ", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}
}