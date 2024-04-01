package com.fuzzy.subsystem.common.loginservercon.gspackets;

public class PointConnectionG extends GameServerBasePacket {
    public PointConnectionG(String acc, int point) {
        writeC(0x1d);
        writeS(acc);
        writeD(point);
    }
}