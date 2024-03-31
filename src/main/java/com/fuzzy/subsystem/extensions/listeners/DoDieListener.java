package com.fuzzy.subsystem.extensions.listeners;

import l2open.extensions.listeners.events.MethodEvent;
import l2open.gameserver.model.L2Character;

/**
 * User: Death
 */
/**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public abstract class DoDieListener implements MethodInvokeListener
{
	@Override
	public final void methodInvoked(MethodEvent e)
	{
		onDie((L2Character) e.getOwner());
	}

	/**
	 * Простенький фильтр. Фильтрирует по названии метода и аргументам.
	 * Ничто не мешает переделать при нужде :)
	 *
	 * @param event событие с аргументами
	 *
	 * @return true если все ок ;)
	 */
	@Override
	public final boolean accept(MethodEvent event)
	{
		return event.getMethodName().equals(MethodCollection.onDecay);
	}

	public abstract void onDie(L2Character cha);
}