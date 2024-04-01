package com.fuzzy.subsystem.gameserver.serverpackets;

/**
 * @author SYS
 * @date 10/9/2007
 * Format: cdс
 * Пример: CF 81 DD 24 00 01
 */
public class EventTrigger extends L2GameServerPacket
{
	// Ловушки в замках.
	// ID x y z (координаты примерные)

	// Годдард
	// 2416001 149689 -47482 -1768
	// 2416002 145344 -47532 -1769

	// Дион
	// 2022001 22068 158706 -2781 // на главном входе
	// 2022002 22988 162470 -2791 // сзади замка

	// Гиран
	// 2322001 114929 145069 -2661 // на главном входе
	// 2322002 118639 144166 -2664 // сзади замка

	// Глудио
	// 1921001 -18053 110832 -2594 // на главном входе
	// 1921002 -19040 107278 -2597 // сзади замка

	// Иннадрил
	// 2325001 116044 247496 -885 // на главном входе
	// 2325002 116970 251248 -887 // сзади замка

	// Аден
	// 2418001 145692 6918 -496 // левая
	// 2418002 149208 7051 -496 // правая

	// Не проверенные, но скорее всего рабочие :)

	// Шуттгард
	// 2213001
	// 2213002

	// Орен
	// 2219001
	// 2219002

	// Руна
	// 2016001
	// 2016002

	private int _trapId;
	private boolean _active;

	public EventTrigger(int trapId, boolean active)
	{
		_trapId = trapId;
		_active = active;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xCF);
		writeD(_trapId); // trap object id
		writeC(_active ? 1 : 0); // trap activity 1 or 0
	}
}