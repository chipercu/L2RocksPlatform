package com.fuzzy.subsystem.gameserver.instancemanager;

import javolution.util.FastMap;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;
import com.fuzzy.subsystem.util.GArray;

public class PlayerMessageStack
{
	private static PlayerMessageStack _instance;

	private final FastMap<Integer, GArray<L2GameServerPacket>> _stack = new FastMap<Integer, GArray<L2GameServerPacket>>();

	public static PlayerMessageStack getInstance()
	{
		if(_instance == null)
			_instance = new PlayerMessageStack();
		return _instance;
	}

	public PlayerMessageStack()
	{
	//TODO: загрузка из БД
	}

	public void mailto(int char_obj_id, L2GameServerPacket message)
	{
		L2Player cha = L2ObjectsStorage.getPlayer(char_obj_id);
		if(cha != null)
		{
			cha.sendPacket(message);
			return;
		}

		synchronized (_stack)
		{
			GArray<L2GameServerPacket> messages;
			if(_stack.containsKey(char_obj_id))
				messages = _stack.remove(char_obj_id);
			else
				messages = new GArray<L2GameServerPacket>();
			messages.add(message);
			//TODO: сохранение в БД
			_stack.put(char_obj_id, messages);
		}
	}

	public void CheckMessages(L2Player cha)
	{
		GArray<L2GameServerPacket> messages = null;
		synchronized (_stack)
		{
			if(!_stack.containsKey(cha.getObjectId()))
				return;
			messages = _stack.remove(cha.getObjectId());
		}
		if(messages == null || messages.size() == 0)
			return;
		//TODO: удаление из БД
		for(L2GameServerPacket message : messages)
			cha.sendPacket(message);
	}
}