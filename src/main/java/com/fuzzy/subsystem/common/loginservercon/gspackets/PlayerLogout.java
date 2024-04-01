package com.fuzzy.subsystem.common.loginservercon.gspackets;

public class PlayerLogout extends GameServerBasePacket {
    public PlayerLogout(String player) {
        writeC(0x03);
        writeS(player);
    }
}