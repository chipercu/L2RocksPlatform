package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;

public class PlayersInGame extends ClientBasePacket {
    public PlayersInGame(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    private String[] accs;
    private int player_conut;

    @Override
    public void read() {
        player_conut = readH();
        accs = new String[readH()];
        for (int i = 0; i < accs.length; i++)
            accs[i] = readS();
        getGameServer().addAccountsInGameServer(accs);
        getGameServer().setPlayerCount(player_conut);
    }
}