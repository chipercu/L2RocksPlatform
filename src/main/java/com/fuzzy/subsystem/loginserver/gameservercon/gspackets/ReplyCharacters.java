package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;

public class ReplyCharacters extends ClientBasePacket {
    String _account;
    int _chars;
    long[] _charsList;
    int _serverId;

    public ReplyCharacters(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    public void read() {
        _account = readS();
        _chars = readC();
        int charsToDel = readC();
        _charsList = new long[charsToDel];
        for (int i = 0; i < charsToDel; i++)
            _charsList[i] = readQ();
        _serverId = getGameServer().getServerId();
        LoginController.getInstance().setCharactersOnServer(_account, _chars, _charsList, _serverId);
    }
}