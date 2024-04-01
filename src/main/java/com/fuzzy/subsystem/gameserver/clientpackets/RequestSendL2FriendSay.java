package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.gameserver.serverpackets.L2FriendSay;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.tables.FriendsTable;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;
import com.fuzzy.subsystem.util.LogChat;

/**
 * Recieve Private (Friend) Message
 * Format: c SS
 * S: Message
 * S: Receiving Player
 */
public class RequestSendL2FriendSay extends L2GameClientPacket
{
	private String _message;
	private String _reciever;

	@Override
	public void readImpl()
	{
		_message = readS(2048);
		_reciever = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getNoChannel() != 0)
		{
			if(activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
			{
				activeChar.sendPacket(Msg.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_BECOME_EVEN_LONGER);
				return;
			}
			PlayerData.getInstance().updateNoChannel(activeChar, 0);
		}

		L2Player targetPlayer = L2World.getPlayer(_reciever);
		if(targetPlayer == null)
		{
			activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_ONLINE);
			return;
		}

		if(!FriendsTable.getInstance().checkIsFriends(activeChar.getObjectId(), targetPlayer.getObjectId()))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(_reciever));
			return;
		}

		if(targetPlayer.isBlockAll())
		{
			activeChar.sendPacket(Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE);
			return;
		}

		LogChat.add(_message, "PRIV_MSG", activeChar.getName(), _reciever);

		targetPlayer.sendPacket(new L2FriendSay(activeChar.getName(), _reciever, _message));
	}
}