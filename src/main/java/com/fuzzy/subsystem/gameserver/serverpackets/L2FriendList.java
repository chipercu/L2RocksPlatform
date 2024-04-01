package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.database.DatabaseUtils;
import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.util.GArray;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L2FriendList extends L2GameServerPacket
{
	private static Logger _log = Logger.getLogger(L2FriendList.class.getName());
	private GArray<FriendInfo> friends = new GArray<FriendInfo>();
	private L2Player _cha;
	private boolean _message = false;
	private boolean _packet = false;

	public L2FriendList(L2Player cha)
	{
		_cha = cha;
		_message = true;
		_packet = false;
		common();
	}

	public L2FriendList(L2Player cha, boolean sendMessage)
	{
		_cha = cha;
		_message = sendMessage;
		_packet = true;
		common();
	}

	private void common()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		FriendInfo friendinfo;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT friend_id, char_name FROM character_friends LEFT JOIN characters ON ( character_friends.friend_id = characters.obj_Id ) WHERE char_id=?");
			statement.setInt(1, _cha.getObjectId());
			rset = statement.executeQuery();

			if(_message)
				_cha.sendPacket(Msg._FRIENDS_LIST_);
			while(rset.next())
			{
				friendinfo = new FriendInfo(rset.getString("char_name"));
				if(friendinfo.name == null)
					continue;
				L2Player friend = L2World.getPlayer(friendinfo.name);
				if(friend == null)
				{
					if(_message)
						_cha.sendPacket(new SystemMessage(SystemMessage.S1_CURRENTLY_OFFLINE).addString(friendinfo.name));
				}
				else
				{
					if(_message)
						_cha.sendPacket(new SystemMessage(SystemMessage.S1_CURRENTLY_ONLINE).addString(friendinfo.name));
					friendinfo.id = friend.getObjectId();
				}
				friends.add(friendinfo);
			}
			if(_message)
				_cha.sendPacket(Msg.__EQUALS__);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "Error in friendlist ", e);
			_packet = false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(!_packet)
			return;

		writeC(0x75);
		writeD(friends.size());
		for(FriendInfo friend : friends)
		{
			writeD(0);
			writeS(friend.name);
			writeD(friend.id > 0 ? 1 : 0); //online or offline
			writeD(friend.id); //object_id
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x75);
		writeD(friends.size());
		for(FriendInfo friend : friends)
		{
			writeD(friend.id);
			writeS(friend.name);
			writeD(friend.id > 0 ? 1 : 0); //online or offline
			writeD(friend.id);

			writeD(0); // getLevel
			writeD(0); // getClassId
			writeS(""); // getMemo
		}
		return true;
	}

	private class FriendInfo
	{
		public final String name;
		public int id = 0;

		public FriendInfo(String _name)
		{
			name = _name;
		}
	}

	public String getType()
	{
		return "[S] L2FriendList["+_packet+"]";
	}
}