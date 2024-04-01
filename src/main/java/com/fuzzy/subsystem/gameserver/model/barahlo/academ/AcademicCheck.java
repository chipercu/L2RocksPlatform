package com.fuzzy.subsystem.gameserver.model.barahlo.academ;

import com.fuzzy.subsystem.common.RunnableImpl;
import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.barahlo.academ.dao.AcademiciansDAO;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowScreenMessage;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class AcademicCheck extends RunnableImpl
{
	@Override
	public void runImpl() throws Exception
	{
		for(Academicians academic : AcademiciansStorage.getInstance().get())
		{
			if(academic.getTime() < System.currentTimeMillis())
			{
				AcademyRequest academy = AcademyStorage.getInstance().getReguest(academic.getClanId());

				if(academy != null)
				{
					AcademiciansDAO.getInstance().delete(academic);
					AcademyStorage.getInstance().updateList();
					academy.updateSeats();
				}
			}
			else
			{
				L2Player player = L2ObjectsStorage.getPlayer(academic.getObjId());
				if(player != null)
				{
					int time = (int) ((academic.getTime() - System.currentTimeMillis()) / 1000);
					if(time / 3600 < 1)
						player.sendPacket(new ExShowScreenMessage("Сообщение: " + time / 3600 + " ч.", 3000, ScreenMessageAlign.TOP_CENTER, true));
				}
			}
		}

		ThreadPoolManager.getInstance().schedule(this, 60000L);
	}
}