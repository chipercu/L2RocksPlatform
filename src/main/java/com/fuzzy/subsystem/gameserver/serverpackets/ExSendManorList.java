package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.CastleManager;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Residence;

import java.util.Collection;

/**
 * Format : (h) d [dS]
 * h  sub id
 *
 * d: number of manors
 * [
 * d: id
 * S: manor name
 * ]
 *
 * Пример с оффа(828 протокол):
 * 0000: fe 22 00 09 00 00 00 01 00 00 00 67 00 6c 00 75    .".........g.l.u
 * 0010: 00 64 00 69 00 6f 00 00 00 02 00 00 00 64 00 69    .d.i.o.......d.i
 * 0020: 00 6f 00 6e 00 00 00 03 00 00 00 67 00 69 00 72    .o.n.......g.i.r
 * 0030: 00 61 00 6e 00 00 00 04 00 00 00 6f 00 72 00 65    .a.n.......o.r.e
 * 0040: 00 6e 00 00 00 05 00 00 00 61 00 64 00 65 00 6e    .n.......a.d.e.n
 * 0050: 00 00 00 06 00 00 00 69 00 6e 00 6e 00 61 00 64    .......i.n.n.a.d
 * 0060: 00 72 00 69 00 6c 00 65 00 00 00 07 00 00 00 67    .r.i.l.e.......g
 * 0070: 00 6f 00 64 00 61 00 72 00 64 00 00 00 08 00 00    .o.d.a.r.d......
 * 0080: 00 72 00 75 00 6e 00 65 00 00 00 09 00 00 00 73    .r.u.n.e.......s
 * 0090: 00 68 00 75 00 74 00 74 00 67 00 61 00 72 00 74    .h.u.t.t.g.a.r.t
 * 00a0: 00 00 00                                           ...
 */
public class ExSendManorList extends L2GameServerPacket
{
	private static Collection<Castle> _castles = CastleManager.getInstance().getCastles().values();

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x22);
		writeD(_castles.size());
		for(Residence castle : _castles)
		{
			writeD(castle.getId());
			writeS(castle.getName().toLowerCase());
		}
	}
}