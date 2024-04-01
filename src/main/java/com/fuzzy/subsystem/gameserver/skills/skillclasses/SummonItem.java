package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.instances.L2PetInstance;
import com.fuzzy.subsystem.gameserver.model.items.Inventory;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.gameserver.xml.ItemTemplates;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

public class SummonItem extends L2Skill
{
	private final short _itemId;
	private final int _minId;
	private final int _maxId;
	private final int _minCount;
	private final int _maxCount;

	public SummonItem(final StatsSet set)
	{
		super(set);

		_itemId = set.getShort("SummonItemId", (short) 0);
		_minId = set.getInteger("SummonMinId", 0);
		_maxId = set.getInteger("SummonMaxId", _minId);
		_minCount = set.getInteger("SummonMinCount");
		_maxCount = set.getInteger("SummonMaxCount", _minCount);
	}

	@Override
	public void useSkill(final L2Character activeChar, final GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				Inventory inventory;
				if(target.isPlayer())
					inventory = ((L2Player) target).getInventory();
				else if(target.isPet())
					inventory = ((L2PetInstance) target).getInventory();
				else
					continue;

				L2ItemInstance item = ItemTemplates.getInstance().createItem(_minId > 0 ? Rnd.get(_minId, _maxId) : _itemId);

				final int count = Rnd.get(_minCount, _maxCount);
				item.setCount(count);
				activeChar.sendPacket(SystemMessage.obtainItems(item));
				item = inventory.addItem(item);
				activeChar.sendChanges();
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
	}
}