package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.CursedWeaponsManager;
import com.fuzzy.subsystem.gameserver.model.CursedWeapon;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.serverpackets.ExCursedWeaponLocation;
import com.fuzzy.subsystem.gameserver.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Location;

public class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Character activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		GArray<CursedWeaponInfo> list = new GArray<CursedWeaponInfo>();
		for(CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			Location pos = cw.getWorldPosition();
			if(pos != null)
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
		}

		activeChar.sendPacket(new ExCursedWeaponLocation(list));
	}
}