package com.fuzzy.subsystem.gameserver.model.entity;

import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.gameserver.idfactory.IdFactory;
import com.fuzzy.subsystem.gameserver.instancemanager.CoupleManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author evill33t
 */
public class Couple
{
	protected static Logger _log = Logger.getLogger(Couple.class.getName());

	// =========================================================
	// Data Field
	private int _id = 0;
	private int _player1Id = 0;
	private int _player2Id = 0;
	private boolean _maried = false;
	private long _affiancedDate;
	private long _weddingDate;
	private boolean isChanged;

	// =========================================================
	// Constructor
	public Couple(int coupleId)
	{
		_id = coupleId;
	}

	public Couple(L2Player player1, L2Player player2)
	{
		_id = IdFactory.getInstance().getNextId();
		_player1Id = player1.getObjectId();
		_player2Id = player2.getObjectId();
		long time = System.currentTimeMillis();
		_affiancedDate = time;
		_weddingDate = time;
		player1.setCoupleId(_id);
		player1.setPartnerId(_player2Id);
		player2.setCoupleId(_id);
		player2.setPartnerId(_player1Id);
	}

	public void marry()
	{
		_weddingDate = System.currentTimeMillis();
		_maried = true;
		setChanged(true);
	}

	public void divorce()
	{
		CoupleManager.getInstance().getCouples().remove(this);
		CoupleManager.getInstance().getDeletedCouples().add(this);
	}

	public void store(ThreadConnection con)
	{
		FiltredPreparedStatement statement = null;
		try
		{
			statement = con.prepareStatement("REPLACE INTO couples (id, player1Id, player2Id, maried, affiancedDate, weddingDate) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setInt(1, _id);
			statement.setInt(2, _player1Id);
			statement.setInt(3, _player2Id);
			statement.setBoolean(4, _maried);
			statement.setLong(5, _affiancedDate);
			statement.setLong(6, _weddingDate);
			statement.execute();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(statement != null)
				statement.close();
		}
	}

	public final int getId()
	{
		return _id;
	}

	public final int getPlayer1Id()
	{
		return _player1Id;
	}

	public final int getPlayer2Id()
	{
		return _player2Id;
	}

	public final boolean getMaried()
	{
		return _maried;
	}

	public final long getAffiancedDate()
	{
		return _affiancedDate;
	}

	public final long getWeddingDate()
	{
		return _weddingDate;
	}

	public void setPlayer1Id(int _player1Id)
	{
		this._player1Id = _player1Id;
	}

	public void setPlayer2Id(int _player2Id)
	{
		this._player2Id = _player2Id;
	}

	public void setMaried(boolean _maried)
	{
		this._maried = _maried;
	}

	public void setAffiancedDate(long _affiancedDate)
	{
		this._affiancedDate = _affiancedDate;
	}

	public void setWeddingDate(long _weddingDate)
	{
		this._weddingDate = _weddingDate;
	}

	/**
	 * Требует ли изминений свадьба в базе даных
	 * @return true если требует
	 */
	public boolean isChanged()
	{
		return isChanged;
	}

	/**
	 * Устанавливает состояние изменения свадьбы
	 * @param val изменена или нет
	 */
	public void setChanged(boolean val)
	{
		isChanged = val;
	}
}
