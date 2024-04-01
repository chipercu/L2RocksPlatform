package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.L2SkillLearn;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

import java.util.List;

public class ExAcquireSkillInfo extends L2GameServerPacket {
    /**
     * Field skillLearn.
     */
    private final L2SkillLearn skillLearn;
    private final L2Player _player;

    /**
     * Constructor for ExAcquireSkillInfo.
     *
     * @param player Player
     * @param sk     SkillLearn
     */
    public ExAcquireSkillInfo(L2Player player, L2SkillLearn sk) {
        _player = player;
        skillLearn = sk;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xFC);
        writeD(skillLearn.getId());
        writeD(skillLearn.getLevel());
        writeD(skillLearn.getSpCost());
        writeH(skillLearn.getMinLevel());
        writeH(0); // Tauti
        boolean consumeItem = skillLearn.getItemId() > 0;
        writeD(consumeItem ? 1 : 0);
        if (consumeItem) {
            writeD(skillLearn.getItemId());
            writeQ(skillLearn.getItemCount());
        }

        List<L2Skill> delete_skills = skillLearn.getRemovedSkillsForPlayer(_player);
        writeD(delete_skills.size());
        for (L2Skill skill : delete_skills) {
            writeD(skill.getId());// skillId
            writeD(skill.getLevel());// skillLvl
        }
    }
}
