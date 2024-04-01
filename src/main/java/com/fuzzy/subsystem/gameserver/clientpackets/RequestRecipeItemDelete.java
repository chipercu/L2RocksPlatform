package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.RecipeController;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Recipe;
import com.fuzzy.subsystem.gameserver.serverpackets.RecipeBookItemList;

public class RequestRecipeItemDelete extends L2GameClientPacket
{
	// Format: cd

	private int _RecipeID;

	@Override
	public void readImpl()
	{
		_RecipeID = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Recipe rp = RecipeController.getInstance().getRecipeByRecipeId(_RecipeID);

		if(rp == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.unregisterRecipe(_RecipeID);

		RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), (int) activeChar.getCurrentMp());

		response.setRecipes(activeChar.getDwarvenRecipeBook());

		activeChar.sendPacket(response);
	}
}