package com.fuzzy.subsystem.util.reference;

/**
 * Интерфейс хранителя ссылки.
 * 
 * @author G1ta0
 *
 * @param <T>
 */
public interface HardReference<T>
{
	/** Получить объект, который удерживается **/
	public T get();
	
	/** Очистить сылку на удерживаемый объект **/
	public void clear();
}
