package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public interface OnCurrentMpReduceListener extends CharListener
{
	void onCurrentMpReduce(L2Character p0, double p1, L2Character p2);
}
