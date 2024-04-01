package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

/**
 * @author VISTALL
 */
public interface OnReviveListener extends CharListener
{
	public void onRevive(L2Character actor);
}
