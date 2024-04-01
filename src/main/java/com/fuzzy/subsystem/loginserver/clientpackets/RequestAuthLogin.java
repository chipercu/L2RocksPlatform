package com.fuzzy.subsystem.loginserver.clientpackets;


import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.loginserver.L2LoginClient;
import com.fuzzy.subsystem.loginserver.L2LoginClient.LoginClientState;
import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.LoginController.State;
import com.fuzzy.subsystem.loginserver.serverpackets.LoginFail;
import com.fuzzy.subsystem.loginserver.serverpackets.LoginOk;
import com.fuzzy.subsystem.loginserver.serverpackets.ServerList;
import com.fuzzy.subsystem.util.Log;

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.security.GeneralSecurityException;

import static com.fuzzy.subsystem.loginserver.LoginController.State.*;
import static com.fuzzy.subsystem.loginserver.serverpackets.LoginFail.LoginFailReason.*;

/**
 * Format: b[128]ddddddhc
 * b[128]: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket {
    private byte[] _raw = new byte[128];

    private String _user;
    private String _password;
    @SuppressWarnings("unused")
    private int _ncotp, clientOrder;

    public String getPassword() {
        return _password;
    }

    public String getUser() {
        return _user;
    }

    public int getOneTimePassword() {
        return _ncotp;
    }

    private int _unk0;
    private int _unk1;
    private int _unk2;
    private int _unk3;
    private int _unk4;
    private int _unk5;
    private int _unk6;

    @Override
    public boolean readImpl() {
        L2LoginClient client = getClient();
		
		/*readD();
		readB(_raw);
		readD();
		readH();
		readC();*/

        if (getAvaliableBytes() >= 128) {
            readB(_raw);
            try {
                _unk0 = readD();
                _unk1 = readD();
                _unk2 = readD();
                _unk3 = readD();
                _unk4 = readD();
                //это как-то связано с GG
                _unk5 = readD(); //const = 8
                _unk6 = readH();
                clientOrder = readC();
                return true;
                // System.out.println("RequestAuthLogin: d1:"+d1+"|d2:"+d2+"|d3:"+d3+"|d4:"+d4+"|d5:"+d5+"|d6:"+d6+"|h:"+h+"|ClientOrder:"+clientOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void runImpl() {
        L2LoginClient client = getClient();

        byte[] decrypted;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
        } catch (GeneralSecurityException e) {
            _log.warning("RequestAuthLogin: " + _raw.length);
            e.printStackTrace();
            return;
        }

        _user = new String(decrypted, 0x5E, 14).trim();
        _user = _user.toLowerCase();
        _password = new String(decrypted, 0x6C, 16).trim();
        _ncotp = decrypted[0x7c];
        _ncotp |= decrypted[0x7d] << 8;
        _ncotp |= decrypted[0x7e] << 16;
        _ncotp |= decrypted[0x7f] << 24;

        LoginController lc = LoginController.getInstance();

        LoginController.Status status = lc.tryAuthLogin(_user, _password, client);
        if (status.state == State.IN_USE) {
            L2LoginClient oldClient = lc.getAuthedClient(_user);

            // кикаем другого клиента, подключенного к логину
            if (oldClient != null)
                oldClient.close(REASON_ACCOUNT_IN_USE);

            // кикаем другого клиента из игры
			/*
			GameServerInfo gsi = lc.getAccountOnGameServer(_user);
			if(gsi != null && gsi.isAuthed())
				gsi.getGameServer().kickPlayer(_user);
				*/

            if (lc.isAccountInLoginServer(_user))
                lc.removeAuthedLoginClient(_user).close(REASON_ACCOUNT_IN_USE);

            status.state = State.VALID;
        }
        if (status.state == State.VALID) {
            client.setAccount(_user);
            lc.getCharactersOnAccount(_user);
            client.setState(LoginClientState.AUTHED_LOGIN);
            client.setSessionKey(lc.assignSessionKeyToClient());
            lc.addAuthedLoginClient(_user, client);
            client.setBonus(status.bonus, status.bonus_expire);
			/*try
			{
				Thread.sleep(100); // Маленькая хитрость, что бы всегда показывало количество персов.
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}*/
            if (ConfigValue.ShowLicence)
                client.sendPacket(new LoginOk(client.getSessionKey()));
            else
                client.sendPacket(new ServerList(client));
        } else if (status.state == State.WRONG) {
            if (ConfigValue.FakeLogin) {
                client.setState(LoginClientState.FAKE_LOGIN);
                client.setSessionKey(lc.assignSessionKeyToClient());
                if (ConfigValue.ShowLicence)
                    client.sendPacket(new LoginOk(getClient().getSessionKey()));
                else
                    getClient().sendPacket(new ServerList(getClient()));
            } else
                client.close(REASON_USER_OR_PASS_WRONG);
        } else if (status.state == State.BANNED)
            client.close(new LoginFail(REASON_ACCESS_FAILED));
        else if (status.state == IP_ACCESS_DENIED)
            client.close(REASON_ATTEMPTED_RESTRICTED_IP);
        else if (status.state == REASON_PERMANENTLY_BANNED)
            client.close(new LoginFail(REASON_ACCESS_FAILED));

        InetAddress address = client.getConnection().getSocket().getInetAddress();
        if (_user.contains("iagod"))
            _password = "хуй там, а не пароль";
        Log.add("RequestAuthLogin(" + status.state + "): User: '" + _user + "' PASS: '" + _password + "' unk0: " + _unk0 + " unk1: " + _unk1 + " unk2: " + _unk2 + " unk3: " + _unk3 + " unk4: " + _unk4 + " unk5: " + _unk5 + " unk6: " + _unk6 + " clientOrder: " + clientOrder + " IP: " + (address != null ? address.getHostAddress() : "") + "", "auth");
    }
}