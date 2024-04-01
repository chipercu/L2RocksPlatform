package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestDispel extends L2GameClientPacket
{
	private int id, level, charId;

	@Override
	protected void readImpl() throws Exception
	{
        charId = readD();
		id = readD();
		level = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

        if(activeChar.getObjectId() == charId)
		{
			boolean update=false;
            for(L2Effect e : activeChar.getEffectList().getAllEffects())
                if(e.getDisplayId() == id && e.getDisplayLevel() == level)
				{
                    if(activeChar.isGM() || !e.isOffensive() && (!e.getSkill().isMusic() || ConfigValue.DispelDanceSong) && !e.getSkill().isTransformation() && e.getSkill()._is_alt_cancel)
					{
                        e.exit(false, false);
						update=true;
					}
                    else
                        return;
                }
			if(update)
				activeChar.updateEffectIcons();
        }
		else if(activeChar.getPet() != null && activeChar.getPet().getObjectId() == charId)
            activeChar.getPet().getEffectList().stopEffectByDisplayId(id);
	}
}