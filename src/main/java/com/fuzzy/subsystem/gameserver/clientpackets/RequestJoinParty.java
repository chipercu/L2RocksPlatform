package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Party;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.gameserver.model.base.Transaction;
import com.fuzzy.subsystem.gameserver.model.base.Transaction.TransactionType;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;
import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.gameserver.serverpackets.AskJoinParty;
import com.fuzzy.subsystem.gameserver.serverpackets.JoinParty;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

import static com.fuzzy.subsystem.gameserver.model.L2Zone.ZoneType.OlympiadStadia;

public class RequestJoinParty extends L2GameClientPacket {
    private String _name;
    private int _itemDistribution;

    @Override
    public void readImpl() {
        _name = readS(ConfigValue.cNameMaxLen);
        _itemDistribution = readD();
    }

    @Override
    public void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        L2Player target = L2World.getPlayer(_name);

        if (target == null || target == activeChar || target.isInvisible() || target.is_block || activeChar.is_block) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInTransaction()) {
            activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY, Msg.ActionFail);
            return;
        } else if (ConfigValue.NoInvitePartyForPvp && target.getPvpFlag() != 0) {
            activeChar.sendPacket(Msg.INVALID_TARGET(), Msg.ActionFail);
            return;
        }

        SystemMessage problem = target.canJoinParty(activeChar);
        if (problem != null) {
            activeChar.sendPacket(problem);
            return;
        }

        if (!activeChar.isInParty())
            createNewParty(getClient(), _itemDistribution, target, activeChar);
        else
            addTargetToParty(getClient(), _itemDistribution, target, activeChar);
    }

    private void addTargetToParty(L2GameClient client, int itemDistribution, L2Player target, L2Player activeChar) {
        if (activeChar.getParty().getMemberCount() >= ConfigValue.MAX_SIZE) {
            activeChar.sendPacket(Msg.PARTY_IS_FULL);
            return;
        }

        // Только Party Leader может приглашать новых членов
        if (ConfigValue.PartyLeaderOnlyCanInvite && !activeChar.getParty().isLeader(activeChar)) {
            activeChar.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
            return;
        }

        if (activeChar.getParty().isInDimensionalRift()) {
            activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestJoinParty.InDimensionalRift", activeChar));
            activeChar.sendActionFailed();
        }

        if (!target.isInTransaction()) {
            if (target.getIP().equals(activeChar.getIP()) && ConfigValue.AutoTrade){
                SystemMessage problem = target.canJoinParty(activeChar);
                if (problem != null) {
                    target.sendPacket(problem, Msg.ActionFail);
                    activeChar.sendPacket(new JoinParty(0));
                    return;
                }

                if (target.isInZone(OlympiadStadia) && target.i_ai8 != 954356543) {
                    target.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                    activeChar.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                    return;
                } else if (Olympiad.isRegistered(target) || Olympiad.isRegistered(activeChar) || target.getOlympiadGame() != null || target.isInOlympiadMode() || activeChar.getOlympiadGame() != null || activeChar.isInOlympiadMode()) {
                    target.sendPacket(Msg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
                    activeChar.sendPacket(Msg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
                    return;
                } else if (activeChar.getParty().getMemberCount() >= ConfigValue.MAX_SIZE) {
                    target.sendPacket(Msg.PARTY_IS_FULL);
                    activeChar.sendPacket(Msg.PARTY_IS_FULL);
                    return;
                }
                target.joinParty(activeChar.getParty());
            }else {
                new Transaction(TransactionType.PARTY, activeChar, target, 10000);
                target.sendPacket(new AskJoinParty(activeChar.getName(), itemDistribution));
                activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_INVITED_C1_TO_JOIN_YOUR_PARTY).addString(target.getName()));
            }
        } else
            activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
    }

    private void createNewParty(L2GameClient client, int itemDistribution, L2Player target, L2Player requestor) {
        if (!target.isInTransaction()) {
            requestor.setParty(new L2Party(requestor, itemDistribution));

            if (target.getIP().equals(requestor.getIP())&& ConfigValue.AutoTrade){

                SystemMessage problem = target.canJoinParty(requestor);
                if (problem != null) {
                    target.sendPacket(problem, Msg.ActionFail);
                    requestor.sendPacket(new JoinParty(0));
                    return;
                }

                if (target.isInZone(OlympiadStadia) && target.i_ai8 != 954356543) {
                    target.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                    requestor.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                    return;
                } else if (Olympiad.isRegistered(target) || Olympiad.isRegistered(requestor) || target.getOlympiadGame() != null || target.isInOlympiadMode() || requestor.getOlympiadGame() != null || requestor.isInOlympiadMode()) {
                    target.sendPacket(Msg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
                    requestor.sendPacket(Msg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
                    return;
                } else if (requestor.getParty().getMemberCount() >= ConfigValue.MAX_SIZE) {
                    target.sendPacket(Msg.PARTY_IS_FULL);
                    requestor.sendPacket(Msg.PARTY_IS_FULL);
                    return;
                }
                target.joinParty(requestor.getParty());
            }else {
                new Transaction(TransactionType.PARTY, requestor, target, 10000);
                target.sendPacket(new AskJoinParty(requestor.getName(), itemDistribution));
                requestor.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_INVITED_C1_TO_JOIN_YOUR_PARTY).addString(target.getName()));
            }
        } else
            requestor.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
    }
}