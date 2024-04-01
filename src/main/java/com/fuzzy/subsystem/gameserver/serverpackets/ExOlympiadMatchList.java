package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.entity.olympiad.CompType;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.OlympiadGame;
import com.fuzzy.subsystem.util.GArray;

public class ExOlympiadMatchList extends L2GameServerPacket
{
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeHG(0xD4);
		writeD(0x00);
		GArray<OlympiadGame> games = new GArray<OlympiadGame>();
		if(Olympiad.getOlympiadGames() != null)
			for(OlympiadGame game : Olympiad.getActiveGames())
				games.add(game);
		writeD(games != null ? games.size() : 0);
		writeD(0x00);

		if(games != null)
			for(OlympiadGame curGame : games)
			{
				writeD(curGame.getId());
				if(curGame.getType().equals(CompType.NON_CLASSED))
					writeD(0x01);
				else if(curGame.getType().equals(CompType.CLASSED))
					writeD(0x02);
				else if(curGame.getType().equals(CompType.TEAM))
					writeD(-1);
				else
					writeD(0x00);
				writeD(curGame.getState());
				writeS(curGame.getTeam(1).getName());
				writeS(curGame.getTeam(2).getName());
			}
	}
}