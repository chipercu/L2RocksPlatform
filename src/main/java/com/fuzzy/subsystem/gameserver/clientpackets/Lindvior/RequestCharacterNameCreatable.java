package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.ExIsCharNameCreatable;
import com.fuzzy.subsystem.gameserver.tables.CharNameTable;
import com.fuzzy.subsystem.util.Util;

public class RequestCharacterNameCreatable extends L2GameClientPacket {
    private String _nickname;

    @Override
    protected void readImpl() {
        _nickname = readS();
    }

    @Override
    protected void runImpl() {
        if (CharNameTable.getInstance().accountCharNumber(getClient().getLoginName()) >= 8) {
            sendPacket(new ExIsCharNameCreatable(ExIsCharNameCreatable.REASON_TOO_MANY_CHARACTERS));
            return;
        } else if (_nickname.trim().isEmpty() || !Util.isMatchingRegexp(_nickname, ConfigValue.ClanTitleTemplate)) {
            sendPacket(new ExIsCharNameCreatable(ExIsCharNameCreatable.REASON_16_ENG_CHARS));
            return;
        } else if (CharNameTable.getInstance().doesCharNameExist(_nickname)) {
            sendPacket(new ExIsCharNameCreatable(ExIsCharNameCreatable.REASON_NAME_ALREADY_EXISTS));
            return;
        }
        sendPacket(new ExIsCharNameCreatable(ExIsCharNameCreatable.REASON_CREATION_OK));
    }

    @Override
    public String getType() {
        return "[C] D0:B0 RequestCharacterNameCreatable";
    }
}
