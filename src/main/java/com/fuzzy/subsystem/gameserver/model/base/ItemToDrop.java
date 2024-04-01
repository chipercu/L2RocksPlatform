package com.fuzzy.subsystem.gameserver.model.base;

public class ItemToDrop
{
	public int itemId;
	public long count;
	//public boolean isSpoil;
	public boolean isAdena;
	public boolean isStackable;

	public ItemToDrop(int id)
	{
		itemId = id;
	}
}
