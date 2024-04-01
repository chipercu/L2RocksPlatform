package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2ManufactureItem;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.RecipeShopItemInfo;

/**
 * cdd
 */
public class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _manufacturerObjectId;
	private int _recipeId;

	@Override
	public void readImpl()
	{
		_manufacturerObjectId = readD();
		_recipeId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getDuel() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Player target = (L2Player) activeChar.getVisibleObject(_manufacturerObjectId);

		if(target == null || target.getPrivateStoreType() != L2Player.STORE_PRIVATE_MANUFACTURE)
		{
			activeChar.sendActionFailed();
			return;
		}

		long price = -1;
		for(L2ManufactureItem i : target.getCreateList().getList())
			if(i.getRecipeId() == _recipeId)
			{
				price = i.getCost();
				break;
			}

		if(price == -1)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new RecipeShopItemInfo(_manufacturerObjectId, _recipeId, price, 0xFFFFFFFF, activeChar));
	}
}