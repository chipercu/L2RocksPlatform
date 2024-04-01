package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.extensions.network.L2GameClient.GameClientState;
import com.fuzzy.subsystem.gameserver.serverpackets.CharSelected;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;
import com.fuzzy.subsystem.util.AutoBan;
import com.fuzzy.subsystem.util.Util;

public class CharacterSelected extends L2GameClientPacket {
    private int _charSlot;

    /**
     * Format: cdhddd
     */
    @Override
    public void readImpl() {
        _charSlot = readD();
    }

    @Override
    public void runImpl() {
        L2GameClient client = getClient();
        if (ConfigValue.SAEnabled && !client.getSecondaryAuth().isAuthed()) {
            client.getSecondaryAuth().openDialog();
            return;
        } else if (client.getActiveChar() != null)
            return;
        L2Player activeChar = client.loadCharFromDisk(_charSlot);
        if (activeChar == null)
            return;
        else if (ConfigValue.ServerOnlyCreate && !activeChar.isGM()) {
            activeChar.logout(false, false, true, true);
            return;
        } else if (ConfigValue.AccHwidLockClear > 0 && (activeChar.getLastAccess() + ConfigValue.AccHwidLockClear * 86400) < (System.currentTimeMillis() / 1000))
            activeChar.clearAccLock();
        else if (ConfigValue.AccHwidLockEnable && activeChar.getAccLock() != null && !Util.contains(activeChar.getAccLock(), activeChar.getHWIDs())) {
            if (ConfigValue.MultiHwidSystem) {
                PlayerData.getInstance().select_answer(activeChar);
                activeChar.is_block = true;
            }
            if (!activeChar.is_block) {
                activeChar.logout(false, false, false, true);
                return;
            }
        } else if (AutoBan.isBanned(activeChar.getObjectId())) {
            activeChar.setAccessLevel(-100);
            activeChar.logout(false, false, true, true);
            return;
        }
		/*else if(!canLogin(client) && (!ConfigValue.MaxInstancesPremium || !activeChar.hasBonus()))
		{
			activeChar.logout(false, false, true, true);
			// GuardMsgPacket.MsgType.INSTANCE_LIMIT.packet
			return;
		}*/
        if (activeChar.getAccessLevel() < 0)
            activeChar.setAccessLevel(0);
        else if (ConfigValue.CCPGuardEnable) {
//            if (!ccpGuard.Protection.checkPlayerWithHWID(client, activeChar.getObjectId(), activeChar.getName())) {
//                return;
//            }
        }

        //PlayerData.getInstance().select_answer(activeChar);
        //activeChar.is_block = true;

        client.setState(GameClientState.IN_GAME);

        sendPacket(new CharSelected(activeChar, client.getSessionId().playOkID1));
    }

	/*public boolean canLogin(L2GameClient client)
	{
		if(ConfigValue.MaxInstances <= 0)
			return true;

		com.l2scripts.sguard.core.manager.session.GuardSession gs = com.l2scripts.sguard.core.manager.GuardSessionManager.getSession(com.l2scripts.sguard.core.manager.session.HWID.fromString(new com.l2scripts.sguard.api.GuardPlayer(client).getHWID()));

		int count = gs.getCount();

		if(gs.hasAccountSession(client.getLoginName()))
			return true;

		return count < ConfigValue.MaxInstances;
	}*/
}