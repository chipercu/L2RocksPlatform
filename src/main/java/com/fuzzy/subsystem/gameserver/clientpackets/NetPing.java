package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.GameStart;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket {
    private int corectPing;
    private int clientSesionId;
    private int ping;

    @Override
    public void runImpl() {
        //_log.info(getType() + " :: " + ping + " :: " + corectPing + " :: " + clientSesionId + " :: "+System.currentTimeMillis()+" :: "+(System.currentTimeMillis()-1362277045339L));
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        if (getClient().ping_send == 0) {
            activeChar.sendMessage("NetPing: " + ((System.currentTimeMillis() - ping - GameStart.serverUpTime()) / 2) + " corect: " + corectPing + " SID: " + clientSesionId);
            getClient().ping_send = 1;
        }
        //_log.info("DelayNetPing: "+(System.currentTimeMillis()-SelectorThread._timeStart));
        getClient().pingTime = (System.currentTimeMillis() - ping - GameStart.serverUpTime());
        getClient().onNetPing(ping);
    }

    @Override
    public void readImpl() {
        ping = readD();
        corectPing = readD();
        clientSesionId = readD();
    }
}