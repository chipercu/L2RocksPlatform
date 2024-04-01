package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public interface OnAttackListener extends CharListener
{
	public void onAttack(L2Character actor, L2Character target);
}
