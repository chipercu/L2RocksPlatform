package com.fuzzy.subsystem.gameserver.model.entity.siege.fortress;

import com.fuzzy.subsystem.database.DatabaseUtils;
import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Fortress;
import com.fuzzy.subsystem.gameserver.model.entity.siege.Siege;
import com.fuzzy.subsystem.gameserver.model.entity.siege.SiegeDatabase;

public class FortressSiegeDatabase extends SiegeDatabase
{
	public FortressSiegeDatabase(Siege siege)
	{
		super(siege);
	}

	@Override
	public void saveSiegeDate()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE forts SET siegeDate = ? WHERE id = ?");
			statement.setLong(1, _siege.getSiegeDate().getTimeInMillis() / 1000);
			statement.setInt(2, _siege.getSiegeUnit().getId());
			statement.execute();
			((Fortress) _siege.getSiegeUnit()).setSiegeDate((int) (_siege.getSiegeDate().getTimeInMillis() / 1000));
		}
		catch(Exception e)
		{
			System.out.println("Exception: saveSiegeDate(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	@Override
	public void saveLastSiegeDate()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE forts SET lastSiegeDate = ? WHERE id = ?");
			statement.setLong(1, _siege.getSiegeUnit().getLastSiegeDate());
			statement.setInt(2, _siege.getSiegeUnit().getId());
			statement.execute();
		}
		catch(Exception e)
		{
			System.out.println("Exception: saveSiegeDate(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}