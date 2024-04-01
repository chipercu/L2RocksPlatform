package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.AugmentName;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.TradeItem;
import com.fuzzy.subsystem.gameserver.model.base.Transaction;
import com.fuzzy.subsystem.gameserver.model.base.Transaction.TransactionType;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.TradeOtherAdd;
import com.fuzzy.subsystem.gameserver.serverpackets.TradeOwnAdd;
import com.fuzzy.subsystem.gameserver.serverpackets.TradeUpdate;
import com.fuzzy.subsystem.util.SafeMath;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AddTradeItem extends L2GameClientPacket
{
	//Format: cddd

	@SuppressWarnings("unused")
	private int _tradeId, _objectId;
	private long _amount;

	@Override
	public void readImpl()
	{
		_tradeId = readD(); // 1 ?
		_objectId = readD();
		_amount = readQ();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || _amount < 1 || activeChar.is_block)
			return;

		Transaction transaction = activeChar.getTransaction();

		if(transaction == null)
			return;

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.TRADE))
		{
			transaction.cancel();
			activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(activeChar);

		if(transaction.isConfirmed(activeChar) || transaction.isConfirmed(requestor))
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_MOVE_ADDITIONAL_ITEMS_BECAUSE_TRADE_HAS_BEEN_CONFIRMED, Msg.ActionFail);
			return;
		}
		else if(activeChar.getTeam() != 0 || requestor.getTeam() != 0 || activeChar.isInEvent() > 0 || requestor.isInEvent() > 0)
		{
			transaction.cancel();
			return;
		}

		L2ItemInstance InvItem = activeChar.getInventory().getItemByObjectId(_objectId);

		if(InvItem == null || !InvItem.canBeTraded(activeChar))
		{
			activeChar.sendPacket(Msg.THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD);
			return;
		}

		long InvItemCount = InvItem.getCount();

		TradeItem tradeItem = getItem(_objectId, transaction.getExchangeList(activeChar));

		long realCount = Math.min(_amount, InvItemCount);
		long leaveCount = InvItemCount - realCount;

		if(tradeItem == null)
		{
			// добавляем новую вещь в список
			tradeItem = new TradeItem(InvItem);
			tradeItem.setCount(realCount);
			transaction.getExchangeList(activeChar).add(tradeItem);
		}
		else
		{
			// меняем количество уже имеющегося
			if(!InvItem.canBeTraded(activeChar))
				return;
			long TradeItemCount = tradeItem.getCount();
			if(InvItemCount == TradeItemCount) // мы уже предлогаем всё что имеем
				return;

			try
			{
				if(SafeMath.safeAddLong(_amount, TradeItemCount) >= InvItemCount)
					realCount = InvItemCount - TradeItemCount;
			}
			catch(ArithmeticException e)
			{
				activeChar.sendPacket(Msg.SYSTEM_ERROR, Msg.ActionFail);
				return;
			}

			tradeItem.setCount(realCount + TradeItemCount);
			leaveCount = InvItemCount - realCount - TradeItemCount;
		}

		activeChar.sendPacket(new TradeOwnAdd(InvItem, tradeItem.getCount()), new TradeUpdate(InvItem, leaveCount));
		requestor.sendPacket(new TradeOtherAdd(InvItem, tradeItem.getCount()));

		if(ConfigValue.ShowAugmentInfo && InvItem.getAugmentationId() > 0)
		{
			int stat12 = 0x0000FFFF & InvItem.getAugmentation().getAugmentationId();
			int stat34 = InvItem.getAugmentation().getAugmentationId() >> 16;
			String text = InvItem.getName()+" is Augment: "+AugmentName.getString(stat12)+". "+AugmentName.getString(stat34);
			activeChar.sendMessage(text);
			requestor.sendMessage(text);
		}
	}

	private static TradeItem getItem(int objId, ConcurrentLinkedQueue<TradeItem> collection)
	{
		for(TradeItem item : collection)
			if(item.getObjectId() == objId)
				return item;
		return null;
	}
}