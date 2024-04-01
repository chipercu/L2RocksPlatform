package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnAutoSoulShotListener extends CharListener
{
	void onAutoSoulShot(L2Player actor, int itemId, boolean enable);
}
