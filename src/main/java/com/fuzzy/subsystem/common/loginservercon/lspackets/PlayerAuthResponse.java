package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.common.loginservercon.AttLS;
import com.fuzzy.subsystem.common.loginservercon.KickWaitingClientTask;
import com.fuzzy.subsystem.common.loginservercon.LSConnection;
import com.fuzzy.subsystem.common.loginservercon.SessionKey;
import com.fuzzy.subsystem.common.loginservercon.gspackets.PlayerInGame;
import com.fuzzy.subsystem.common.loginservercon.gspackets.PlayerLogout;
import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.gameserver.serverpackets.CharacterSelectionInfo;
import com.fuzzy.subsystem.gameserver.serverpackets.LoginFail;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.Stats;

import java.util.logging.Logger;

public class PlayerAuthResponse extends LoginServerBasePacket {
    private static final Logger log = Logger.getLogger(PlayerAuthResponse.class.getName());

    public PlayerAuthResponse(byte[] decrypt, AttLS loginserver) {
        super(decrypt, loginserver);
    }

    @Override
    public void read() {
        String account = readS();
        boolean authed = readC() == 1;
        int playOkId1 = readD();
        int playOkId2 = readD();
        int loginOkId1 = readD();
        int loginOkId2 = readD();
        String s_bonus = readS();
        readS();
        String account_fields = readS();
        int bonusExpire = readD();
        int _LSId = 0;
        try {
            _LSId = readD();
        } catch (Exception e) {
        }

        float bonus = s_bonus == null || s_bonus.equals("") ? 1 : Float.parseFloat(s_bonus);

        L2GameClient client = getLoginServer().getCon().removeWaitingClient(account);

        if (client != null) {
            client.setLSId(_LSId);
            if (client.getState() != L2GameClient.GameClientState.CONNECTED) {
                log.severe("Trying to authd allready authed client.");
                client.closeNow(true);
                return;
            }

            if (client.getLoginName() == null || client.getLoginName().isEmpty()) {
                client.closeNow(true);
                log.warning("PlayerAuthResponse: empty accname for " + client);
                return;
            }

            SessionKey key = client.getSessionId();

            if (authed)
                if (getLoginServer().isLicenseShown())
                    authed = key.playOkID1 == playOkId1 && key.playOkID2 == playOkId2 && key.loginOkID1 == loginOkId1 && key.loginOkID2 == loginOkId2;
                else
                    authed = key.playOkID1 == playOkId1 && key.playOkID2 == playOkId2;

            if (authed) {
                client.account_fields = StatsSet.unserialize(account_fields);
                client.setAuthed(true);
                client.setState(L2GameClient.GameClientState.AUTHED);
                client.startPingTask();
                if (ConfigValue.RateBonusEnabled || ConfigValue.RateBonusApplyRatesThenServiceDisabled) {
                    client.setBonus(bonus);
                    client.setBonusExpire(bonusExpire);
                }
                getLoginServer().getCon().addAccountInGame(client);
                CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLoginName(), client.getSessionId().playOkID1);
                client.sendPacket(csi);
                client.setCharSelection(csi.getCharInfo());
                sendPacket(new PlayerInGame(client.getLoginName(), getLoginServer().getProtocolVersion(), Stats.getOnline(true)));
            } else {
                //log.severe("Cheater? SessionKey invalid! Login: " + client.getLoginName() + ", IP: " + client.getIpAddr());
                client.sendPacket(new LoginFail(LoginFail.INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT));
                ThreadPoolManager.getInstance().schedule(new KickWaitingClientTask(client), 1000);
                LSConnection.getInstance(client.getLSId()).sendPacket(new PlayerLogout(client.getLoginName()));
                LSConnection.getInstance(client.getLSId()).removeAccount(client);
            }
        }
    }
}