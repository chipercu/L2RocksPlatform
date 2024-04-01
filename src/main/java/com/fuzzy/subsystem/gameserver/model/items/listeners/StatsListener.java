package com.fuzzy.subsystem.gameserver.model.items.listeners;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.items.Inventory;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.skills.funcs.Func;

public final class StatsListener implements PaperdollListener
{
	private Inventory _inv;

	public StatsListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		L2Character owner = _inv.getOwner();
		owner.removeStatsOwner(item);
		if(item.getItem().getAttachedTriggers() != null)
			owner.getPlayer().removeTrigger(item.getItem().getAttachedTriggers());
		if(owner.getPet() != null)
			owner.getPet().removeStatsOwner(item);
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		L2Character owner = _inv.getOwner();
		owner.addStatFuncs(item.getStatFuncs(false));
		if(item.getVisualItemId() > 0)
			owner.addStatFuncs(item.getStatFuncs(true));
		if(item.getItem().getAttachedTriggers() != null)
			owner.getPlayer().addTrigger(item.getItem().getAttachedTriggers());
		if(owner.getPet() != null)
		{
            if(item.isWeapon() && item.getAttributeFuncTemplate() != null && item.getAttributeFuncTemplate().get(0) != null)
			{
                Func f = item.getAttributeFuncTemplate().get(0).getFunc(item);
                owner.getPet().addStatFunc(f);
            }
			else if (item.isArmor())
                for(int i=0; i<6; i++)
                    if(item.getAttributeFuncTemplate() != null && item.getAttributeFuncTemplate().get(i) != null)
					{
                        Func f = item.getAttributeFuncTemplate().get(i).getFunc(item);
                        owner.getPet().addStatFunc(f);
                    }
		}
	}
}