package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.ai.CtrlIntention;
import com.fuzzy.subsystem.gameserver.clientpackets.Say2C;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.Say2;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Rnd;
import com.fuzzy.subsystem.util.reference.*;

public class L2PenaltyMonsterInstance extends L2MonsterInstance
{
	private HardReference<? extends L2Player> owner_ref = HardReferences.emptyRef();

	public L2PenaltyMonsterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public L2Character getMostHated()
	{
		L2Player p = getPtk();
		L2Character p2 = super.getMostHated();
		if(p == null)
			return p2;
		if(p2 == null)
			return p;
		return getDistance3D(p) > getDistance3D(p2) ? p2 : p;
	}

	public void SetPlayerToKill(L2Player ptk)
	{
		setPtk(ptk);
		if(Rnd.chance(80))
			broadcastPacket(new Say2(getObjectId(), Say2C.ALL, getName(), "mmm your bait was delicious"));
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, ptk, 10);
		getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, ptk);
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(Rnd.chance(75))
		{
			Say2 cs = new Say2(getObjectId(), Say2C.ALL, getName(), "I will tell fishes not to take your bait");
			broadcastPacket(cs);
		}
		super.doDie(killer);
	}

	public L2Player getPtk()
	{
		return owner_ref.get();
	}

	public void setPtk(L2Player ptk)
	{
		owner_ref = ptk.getRef();
	}
}