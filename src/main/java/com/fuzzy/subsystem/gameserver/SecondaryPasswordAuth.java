package com.fuzzy.subsystem.gameserver;

import com.fuzzy.subsystem.Base64;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.database.*;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.gameserver.serverpackets.Ex2ndPasswordAck;
import com.fuzzy.subsystem.gameserver.serverpackets.Ex2ndPasswordCheck;
import com.fuzzy.subsystem.gameserver.serverpackets.Ex2ndPasswordVerify;
import com.fuzzy.subsystem.util.Util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Logger;

public class SecondaryPasswordAuth
{
	private final Logger _log = Logger.getLogger(SecondaryPasswordAuth.class.getName());
	private final L2GameClient _activeClient;

	private String _password;
	private int _wrongAttempts;
	private boolean _authed;

	private static final String VAR_PWD = "secauth_pwd";
	private static final String VAR_WTE = "secauth_wte";

	private static final String SELECT_PASSWORD = "SELECT var, value FROM character_secondary_password WHERE account_name=? AND var LIKE 'secauth_%'";
	private static final String INSERT_PASSWORD = "INSERT INTO character_secondary_password VALUES (?, ?, ?)";
	private static final String UPDATE_PASSWORD = "UPDATE character_secondary_password SET value=? WHERE account_name=? AND var=?";
	private static final String INSERT_ATTEMPT = "INSERT INTO character_secondary_password VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";
	// private static final String BAN_ACCOUNT = "UPDATE accounts SET banExpires=? WHERE login=?";

	/**
	 * @param activeClient 
	 */
	public SecondaryPasswordAuth(L2GameClient activeClient)
	{
		_activeClient = activeClient;
		_password = null;
		_wrongAttempts = 0;
		_authed = false;
		loadPassword();
	}

	private void loadPassword()
	{
		String var, value = null;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_PASSWORD);
			statement.setString(1, _activeClient.getLoginName());
			ResultSet rs = statement.executeQuery();
			while(rs.next())
			{
				var = rs.getString("var");
				value = rs.getString("value");

				if(var.equals(VAR_PWD))
					_password = value;
				else if(var.equals(VAR_WTE))
					_wrongAttempts = Integer.parseInt(value);
			}
			statement.close();
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Error while reading password.", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public boolean savePassword(String password)
	{
		if(passwordExist())
		{
			_log.warning("[SecondaryPasswordAuth]" + _activeClient.getLoginName() + " forced savePassword");
			_activeClient.closeNow(true);
			return false;
		}

		if(!validatePassword(password))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}

		password = cryptPassword(password);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_PASSWORD);
			statement.setString(1, _activeClient.getLoginName());
			statement.setString(2, VAR_PWD);
			statement.setString(3, password);
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Error while writing password.", e);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_password = password;
		return true;
	}

	public boolean insertWrongAttempt(int attempts)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_ATTEMPT);
			statement.setString(1, _activeClient.getLoginName());
			statement.setString(2, VAR_WTE);
			statement.setString(3, Integer.toString(attempts));
			statement.setString(4, Integer.toString(attempts));
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Error while writing wrong attempts.", e);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return true;
	}

	public boolean changePassword(String oldPassword, String newPassword)
	{
		if(!passwordExist())
		{
			_log.warning("[SecondaryPasswordAuth]" + _activeClient.getLoginName() + " forced changePassword");
			_activeClient.closeNow(true);
			return false;
		}

		if(!checkPassword(oldPassword, true))
			return false;

		if(!validatePassword(newPassword))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}

		newPassword = cryptPassword(newPassword);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_PASSWORD);
			statement.setString(1, newPassword);
			statement.setString(2, _activeClient.getLoginName());
			statement.setString(3, VAR_PWD);
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Error while reading password.", e);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_password = newPassword;
		_authed = false;
		return true;
	}

	public boolean checkPassword(String password, boolean skipAuth)
	{
		password = cryptPassword(password);

		if(!password.equals(_password))
		{
			_wrongAttempts++;
			if(_wrongAttempts < ConfigValue.SAMaxAttemps)
			{
				_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, _wrongAttempts));
				insertWrongAttempt(_wrongAttempts);
			}
			else
			{
				if(ConfigValue.SABanAccEnabled)
					banAccount(_activeClient.getActiveChar());
				//Log.add(_activeClient.getLoginName() + " - (" + _activeClient.getIpAddr() + ") has inputted the wrong password " + _wrongAttempts + " times in row.", "banned_accounts");
				insertWrongAttempt(0);
				_activeClient.close(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, ConfigValue.SAMaxAttemps));
			}
			return false;
		}
		if(!skipAuth)
		{
			_authed = true;
			_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, _wrongAttempts));
		}
		insertWrongAttempt(0);
		return true;
	}

	private void banAccount(L2Player plyr)
	{
		long banTime = ConfigValue.SABanTime; 
		
		try 
		{ 
			plyr.setAccessLevel(-100);
			ban(plyr, banTime);
			plyr.logout(false, false, true, true);
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Error while banning account.", e);
		}
	}

	private void ban(L2Player actor, long time)
	{
		long date = Calendar.getInstance().getTimeInMillis();
		long endban = date / 1000 + time * 60;
		String msg = "Secondary Password Auth ban L2Player" + actor.getName() + " on " + time + " sec";
		
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, actor.getAccountName());
			statement.setInt(2, actor.getObjectId());
			statement.setString(3, "SU");
			statement.setString(4, "SU");
			statement.setString(5, msg);
			statement.setString(6, "SU");
			statement.setLong(7, endban);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("could not store bans data:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
	
	public boolean passwordExist()
	{
		return _password == null ? false : true;
	}

	public void openDialog()
	{
		if(passwordExist())
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_PROMPT));
		else
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_NEW));
	}

	public boolean isAuthed()
	{
		return _authed;
	}

	private String cryptPassword(String password)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			return Base64.encodeBytes(hash);
		}
		catch(NoSuchAlgorithmException e)
		{
			_log.severe("[SecondaryPasswordAuth]Unsupported Algorythm");
		}
		catch(UnsupportedEncodingException e)
		{
			_log.severe("[SecondaryPasswordAuth]Unsupported Encoding");
		}
		return null;
	}

	private boolean validatePassword(String password)
	{
		if(!Util.isDigit(password))
			return false;

		if(password.length() < 6 || password.length() > 8)
			return false;

		if(ConfigValue.SAStrongPass)
		{
			for(int i = 0; i < password.length() - 1; i++)
			{
				char curCh = password.charAt(i);
				char nxtCh = password.charAt(i + 1);

				if(curCh + 1 == nxtCh)
					return false;
				else if(curCh - 1 == nxtCh)
					return false;
				else if(curCh == nxtCh)
					return false;
			}
			for(int i = 0; i < password.length() - 2; i++)
			{
				String toChk = password.substring(i + 1);
				StringBuffer chkEr = new StringBuffer(password.substring(i, i + 2));

				if(toChk.contains(chkEr))
					return false;
				else if(toChk.contains(chkEr.reverse()))
					return false;
			}
		}
		_wrongAttempts = 0;
		return true;
	}
}