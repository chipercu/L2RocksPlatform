package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_restoration;[beast_soul_shot];500}
 * @i_restoration
 * @[beast_soul_shot] - имя итема
 * @500 - количество.
 **/
	
public class i_restoration extends L2Effect
{
	private int _item_id;
	private long _item_count;

	public i_restoration(Env env, EffectTemplate template, Integer item_id, Long item_count)
	{
		super(env, template);
		_instantly = true;
		
		_item_id = item_id;
		_item_count = item_count;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		L2ItemInstance item = getEffected().getPlayer().getInventory().addItem(_item_id, _item_count);
		getEffected().sendPacket(SystemMessage.obtainItems(item));
		getEffected().sendChanges();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
