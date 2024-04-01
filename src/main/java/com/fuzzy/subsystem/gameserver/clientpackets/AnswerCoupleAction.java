package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.ai.L2PlayableAI.nextAction;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.util.Location;
/**
 * @author : Ragnarok
 * @date : 19.12.10    13:05
 */
public class AnswerCoupleAction extends L2GameClientPacket
{
    private int requesterId;
    private int answer;
    private int coupleId;

    @Override
    public void readImpl()
	{
        coupleId = readD();// ид социалки
        answer = readD();// ответ, 0 - не принял, 1 - принял.
        requesterId = readD();// objId того, кто запрашивал
    }

    @Override
    public void runImpl()
	{
        L2Player requester = null;
        L2Player cha = getClient().getActiveChar();
        requester = L2ObjectsStorage.getPlayer(requesterId);

        if (requester == null || cha == null)
            return;

        if (answer == 0)
            cha.sendPacket(new SystemMessage(3119));
		else if (answer == 1)
		{
            double distance = cha.getDistance(requester);
            if (distance > 2000 || distance < 70)
			{
                cha.sendPacket(new SystemMessage(3120));
                requester.sendPacket(new SystemMessage(3120));
                return;
            }

			Location loc = requester.applyOffset(cha.getLoc(), 25);
			loc = GeoEngine.moveCheck(requester.getX(), requester.getY(), requester.getZ(), loc.x, loc.y, requester.getGeoIndex());
			requester.moveToLocation(loc.x, loc.y, loc.z, 0, false);
			requester.getAI().setNextAction(nextAction.COUPLE_ACTION, cha, coupleId, true, false);
        }
		else if (answer == -1)
		{
            SystemMessage sm = new SystemMessage(3164);
            sm.addName(cha);
            requester.sendPacket(sm);
        }
    }

    @Override
    public String getType() 
	{
        return "[C] D0:78 AnswerCoupleAction";
    }
}
