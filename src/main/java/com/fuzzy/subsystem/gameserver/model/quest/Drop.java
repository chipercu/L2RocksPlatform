/**
 * 
 */
package com.fuzzy.subsystem.gameserver.model.quest;

import com.fuzzy.subsystem.util.GArray;

public class Drop
{
	public int condition;
	public int maxcount;
	public int chance;

	public GArray<Short> itemList = new GArray<Short>();

	public Drop(Integer _condition, Integer _maxcount, Integer _chance)
	{
		condition = _condition;
		maxcount = _maxcount;
		chance = _chance;
	}

	public Drop addItem(Short item)
	{
		itemList.add(item);
		return this;
	}
}