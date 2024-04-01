package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.barahlo.Friend;
import com.fuzzy.subsystem.gameserver.tables.FriendsTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FriendList extends L2GameServerPacket
{
	private List<FriendInfo> _friends = Collections.emptyList();

	public FriendList(L2Player player)
	{
		Map<Integer, Friend> friends = FriendsTable.getInstance().select(player);
		_friends = new ArrayList<FriendInfo>(friends.size());
		for(Entry entry : friends.entrySet())
		{
			Friend friend = (Friend)entry.getValue();
			FriendInfo f = new FriendInfo();
			f.name = friend.getName();
			f.classId = friend.getClassId();
			f.objectId = ((Integer)entry.getKey()).intValue();
			f.level = friend.getLevel();
			f.online = friend.isOnline();
			_friends.add(f);
		}
	}

	protected void writeImpl()
	{
		writeC(0x58);
		writeD(_friends.size());
		for(FriendInfo f : _friends)
		{
			writeD(f.objectId);
			writeS(f.name);
			writeD(f.online ? 1 : 0);
			writeD(f.online ? f.objectId : 0);
			writeD(f.classId);
			writeD(f.level);
		}
	}

	private class FriendInfo
	{
		public String name;
		public int objectId;
		public boolean online;
		public int level;
		public int classId;
	}
}