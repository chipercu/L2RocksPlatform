package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:38
 */
public class AnswerPartyLootModification extends L2GameClientPacket {
    int answer;

    @Override
    protected void readImpl() {
        answer = readD();// 0 - не принял, 1 - принял
    }

    @Override
    protected void runImpl() {
        L2Player player = getClient().getActiveChar();
        if (player == null || player.getParty() == null)
            return;
        player.getParty().answerLootModification(player, answer);
    }
}
