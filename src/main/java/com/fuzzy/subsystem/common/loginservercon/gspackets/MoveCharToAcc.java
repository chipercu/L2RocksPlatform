package com.fuzzy.subsystem.common.loginservercon.gspackets;

/**
 * @Author: Abaddon
 */
public class MoveCharToAcc extends GameServerBasePacket {
    public MoveCharToAcc(String player, String oldacc, String newacc, String pass) {
        writeC(0x0c);
        writeS(player);
        writeS(oldacc);
        writeS(newacc);
        writeS(pass);
    }
}