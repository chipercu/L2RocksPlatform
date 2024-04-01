package com.fuzzy.subsystem.common.loginservercon.gspackets;

public class ChangeAccountPoints extends GameServerBasePacket {
    public ChangeAccountPoints(String account, int points, String comments) {
        writeC(0x0c);
        writeD(points);
        writeS(account);
        writeS(comments);
    }
}