package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.util.Location;

public interface OnMoveListener extends CharListener
{
	void onMove(final L2Character p0, final Location p1);
}
