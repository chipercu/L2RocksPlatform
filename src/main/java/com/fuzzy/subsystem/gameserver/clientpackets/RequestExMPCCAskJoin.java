package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2CommandChannel;
import com.fuzzy.subsystem.gameserver.model.L2Party;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.gameserver.model.base.Transaction;
import com.fuzzy.subsystem.gameserver.model.base.Transaction.TransactionType;
import com.fuzzy.subsystem.gameserver.serverpackets.ExAskJoinMPCC;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

/**
 * Format: (ch) S
 */
public class RequestExMPCCAskJoin extends L2GameClientPacket
{
	private String _name;

	/**
	 * @param buf
	 * @param client
	 */
	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(!activeChar.isInParty())
		{
			activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
			return;
		}

		L2Player target = L2World.getPlayer(_name);

		// Чар с таким именем не найден в мире
		if(target == null)
		{
			activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		// Сам себя нельзя
		if(activeChar == target)
        {
			activeChar.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
            activeChar.sendActionFailed();
			return;
        }
		
		// Своей пати нельзя кидать инвайт
		for(L2Player member : activeChar.getParty().getPartyMembers())
			if(member == target)
			{
				activeChar.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
				return;
			}

		// Нельзя приглашать безпартийных
		if(!target.isInParty())
		{
			activeChar.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
			return;
		}

		// Если приглашен в СС не лидер партии, то посылаем приглашение лидеру его партии
		if(target.isInParty() && !target.getParty().isLeader(target))
			target = target.getParty().getPartyLeader();

		if(target == null)
		{
			activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		if(target.getParty() != null && target.getParty().isInCommandChannel())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL).addString(target.getName()));
			return;
		}

		// Чувак уже отвечает на какое-то приглашение
		if(target.isInTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
			return;
		}

		L2Party activeParty = activeChar.getParty();

		if(activeParty.isInCommandChannel())
		{
			// Приглашение в уже существующий СС
			// Приглашать в СС может только лидер CC
			if(activeParty.getCommandChannel().getChannelLeader() != activeChar)
			{
				activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
				return;
			}

			sendInvite(activeChar, target);
		}
		else // СС еще не существует. Отсылаем запрос на инвайт и в случае согласия создаем канал
			if(L2CommandChannel.checkAuthority(activeChar))
				sendInvite(activeChar, target);
	}

	private void sendInvite(L2Player requestor, L2Player target)
	{
		new Transaction(TransactionType.CHANNEL, requestor, target, 30000);
		target.sendPacket(new ExAskJoinMPCC(requestor.getName()));
		requestor.sendMessage("You invited " + target.getName() + " to your Command Channel.");
	}
}