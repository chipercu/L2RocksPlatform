package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManager;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManorManager;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManorManager.SeedProduction;
import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.model.instances.L2ManorManagerInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.StatusUpdate;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.L2Item;
import com.fuzzy.subsystem.gameserver.xml.ItemTemplates;
import com.fuzzy.subsystem.util.Log;
import com.fuzzy.subsystem.util.SafeMath;
import com.fuzzy.subsystem.util.Util;

/**
 * Format: cdd[dd]
 * c    // id (0xC5)
 *
 * d    // manor id
 * d    // seeds to buy
 * [
 * d    // seed id
 * d    // count
 * ]
 */
public class RequestBuySeed extends L2GameClientPacket
{
	private int _count, _manorId;

	private long[] _items; // size _count * 2

	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_count = readD();

		if(_count > Short.MAX_VALUE || _count <= 0 || _count * 12 > _buf.remaining())
		{
			_count = 0;
			return;
		}

		_items = new long[_count * 2];

		for(int i = 0; i < _count; i++)
		{
			_items[i * 2 + 0] = readD();
			_items[i * 2 + 1] = readQ();
			if(_items[i * 2 + 0] < 1 || _items[i * 2 + 1] < 1)
			{
				_count = 0;
				_items = null;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		long totalPrice = 0;
		int slots = 0;
		int totalWeight = 0;

		L2Player player = getClient().getActiveChar();
		if(player == null || player.is_block)
			return;

		if(_count < 1 || _items == null)
		{
			player.sendActionFailed();
			return;
		}

		L2Object target = player.getTarget();

		if(!(target instanceof L2ManorManagerInstance))
			target = player.getLastNpc();

		if(!(target instanceof L2ManorManagerInstance))
			return;

		Castle castle = CastleManager.getInstance().getCastleByIndex(_manorId);

		for(int i = 0; i < _count; i++)
		{
			int seedId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			long price = 0;
			long residual = 0;

			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			price = seed.getPrice();
			residual = seed.getCanProduce();

			if(price <= 0)
				return;

			if(residual < count)
				return;

			try
			{
				totalPrice = SafeMath.safeAddLong(totalPrice, SafeMath.safeMulLong(count, price));
			}
			catch(ArithmeticException e)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Long.MAX_VALUE + " adena worth of goods.\r\n" + e.getMessage(), "", ConfigValue.IllegalActionPunishment);
				return;
			}

			L2Item template = ItemTemplates.getInstance().getTemplate(seedId);
			totalWeight += count * template.getWeight();
			if(!template.isStackable())
				slots += count;
			else if(player.getInventory().getItemByItemId(seedId) == null)
				slots++;
		}

		if(!player.getInventory().validateWeight(totalWeight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		if(!player.getInventory().validateCapacity(slots))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		// Charge buyer
		if(totalPrice < 0 || player.getAdena() < totalPrice)
		{
			sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(totalPrice, true);

		// Adding to treasury for Manor Castle
		castle.addToTreasuryNoTax((int) totalPrice, false, true);
		Log.add(castle.getName() + "|" + (int) totalPrice + "|BuySeed", "treasury");

		// Proceed the purchase
		for(int i = 0; i < _count; i++)
		{
			int seedId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			if(count < 0)
				count = 0;

			// Update Castle Seeds Amount
			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			seed.setCanProduce(seed.getCanProduce() - count);
			CastleManager.getInstance().getCastleByIndex(_manorId).updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

			// Add item to Inventory and adjust update packet
			player.getInventory().addItem(seedId, count);
			player.sendPacket(SystemMessage.obtainItems(seedId, count, 0));
		}

		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
	}
}