package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * Format: (chd) dddS
 * d: always -1
 * d: player team
 * d: player object id
 * S: player name
 */
public class ExCubeGameAddPlayer extends L2GameServerPacket
{
	L2Player _player;
	boolean _isRedTeam;

	public ExCubeGameAddPlayer(L2Player player, boolean isRedTeam)
	{
		_player = player;
		_isRedTeam = isRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x98 : 0x97);
		writeD(0x01);

		writeD(0xffffffff);

		writeD(_isRedTeam ? 0x01 : 0x00);
		writeD(_player.getObjectId());
		writeS(_player.getName());
	}
}