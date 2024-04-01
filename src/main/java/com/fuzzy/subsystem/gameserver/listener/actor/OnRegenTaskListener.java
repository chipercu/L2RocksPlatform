package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public interface OnRegenTaskListener extends CharListener
{
	public void onAddCp(L2Character actor, double addCp);

	public void onAddHp(L2Character actor, double addHp);

	public void onAddMp(L2Character actor, double addMp);

}
