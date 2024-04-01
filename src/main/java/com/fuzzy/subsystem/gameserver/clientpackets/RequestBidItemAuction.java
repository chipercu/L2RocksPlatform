package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.ItemAuctionManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.ItemBroker.ItemAuction;
import com.fuzzy.subsystem.gameserver.model.entity.ItemBroker.ItemAuctionInstance;

public class RequestBidItemAuction extends L2GameClientPacket
{
	private int _instanceId;
	private long _bid;

	protected void runImpl()
	{
		L2Player activeChar = (getClient()).getActiveChar();

		if(activeChar == null)
		{
			return;
		}
		if(_bid < 0 || (_bid > Long.MAX_VALUE))
		{
			return;
		}
		ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		if(instance == null)
			return;

		ItemAuction auction = instance.getCurrentAuction();
		if(auction != null)
			auction.registerBid(activeChar, _bid);
	}

	protected void readImpl()
	{
		_instanceId = super.readD();
		_bid = super.readQ();
	}
}