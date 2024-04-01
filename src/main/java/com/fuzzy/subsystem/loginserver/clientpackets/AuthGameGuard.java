/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.fuzzy.subsystem.loginserver.clientpackets;


import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.loginserver.L2LoginClient;
import com.fuzzy.subsystem.loginserver.serverpackets.GGAuth;
import com.fuzzy.subsystem.loginserver.serverpackets.LoginFail;

/**
 * @author -Wooden-
 * Format: ddddd
 */
public class AuthGameGuard extends L2LoginClientPacket {
    private int _sessionId;
    private int _data1;
    private int _data2;
    private int _data3;
    private int _data4;

    public int getSessionId() {
        return _sessionId;
    }

    public int getData1() {
        return _data1;
    }

    public int getData2() {
        return _data2;
    }

    public int getData3() {
        return _data3;
    }

    public int getData4() {
        return _data4;
    }

    @Override
    protected boolean readImpl() {
        if (getAvaliableBytes() >= 20) {
            _sessionId = readD();
            _data1 = readD();
            _data2 = readD();
            _data3 = readD();
            _data4 = readD();
            return true;
        }
        return false;
    }

    @Override
    public void runImpl() {
        if (!ConfigValue.GGCheck || _sessionId == getClient().getSessionId()) {
            getClient().setState(L2LoginClient.LoginClientState.AUTHED_GG);
            getClient().sendPacket(new GGAuth(getClient().getSessionId()));
        } else
            getClient().close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
    }
}
