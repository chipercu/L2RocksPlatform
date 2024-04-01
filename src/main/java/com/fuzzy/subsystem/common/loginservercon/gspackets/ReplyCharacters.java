package com.fuzzy.subsystem.common.loginservercon.gspackets;

import java.util.List;

public class ReplyCharacters extends GameServerBasePacket {
    public ReplyCharacters(String account, int chars, List<Long> timeToDel) {
        writeC(0x0F);
        writeS(account);
        writeC(chars);
        writeC(timeToDel.size());
        for (long time : timeToDel) {
            writeQ(time);
        }
    }
}