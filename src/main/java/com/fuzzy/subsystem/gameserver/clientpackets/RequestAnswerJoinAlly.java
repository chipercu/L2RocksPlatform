package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Alliance;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.base.Transaction;
import com.fuzzy.subsystem.gameserver.model.base.Transaction.TransactionType;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;
import com.fuzzy.subsystem.util.Log;

/**
 *  format  c(d)
 */
public class RequestAnswerJoinAlly extends L2GameClientPacket
{
	private int _response;

	@Override
	public void readImpl()
	{
		_response = _buf.remaining() >= 4 ? readD() : 0;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
		{
			Transaction transaction = activeChar.getTransaction();

			if(transaction == null)
				return;

			if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.ALLY))
			{
				transaction.cancel();
				activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
				return;
			}

			L2Player requestor = transaction.getOtherPlayer(activeChar);

			transaction.cancel();

			if(requestor.getAlliance() == null)
				return;

			if(_response == 1)
			{
				L2Alliance ally = requestor.getAlliance();
				activeChar.sendPacket(Msg.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
				activeChar.getClan().setAllyId(requestor.getAllyId());
				PlayerData.getInstance().updateClanInDB(activeChar.getClan());
				ally.addAllyMember(activeChar.getClan(), true);
				ally.broadcastAllyStatus(true);
				Log.add("JOIN_ALLY: ally_name="+ally.getAllyName()+" clan="+activeChar.getClan().getName()+" requestor="+requestor.getName(), "alli_info");
			}
			else
				requestor.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
		}
	}
}