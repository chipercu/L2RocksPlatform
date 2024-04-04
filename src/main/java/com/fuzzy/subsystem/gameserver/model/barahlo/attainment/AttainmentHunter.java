package com.fuzzy.subsystem.gameserver.model.barahlo.attainment;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.util.Rnd;

/**
Attainment13_count = 1440
Attainment13_reward = 57,100,57,1000


# 0-Small, 1-Normal, 2-Large, 3-VeryLarge, 4-Giant
Attainment13_Font = 3

# 1024-TopRightRelative, 2-TopRight, 0-TopLeft, 1-TopCenter, 6-MiddleRight, 4-MiddleLeft, 5-MiddleCenter, 10-BottomRight, 8-BottomLeft, 9-BottomCenter
Attainment13_ScreenPos = 0

# 0-Normal,  1-Shadowed
Attainment13_FontStyle = 1

Attainment13_Color = 0xFF, 0xFF, 0xFF, 0xFF
Attainment13_xy = 0,0
**/
public class AttainmentHunter extends Attainment
{
	public AttainmentHunter(L2Player owner)
	{
		super(owner);
	}

	public void incTime()
	{
		if(_owner.isInOfflineMode())
			return;
		int time = _owner.getVarInt("Attainment13_time", 0);
		if(time >= ConfigValue.Attainment13_count-1)
		{
			if(Rnd.chance(ConfigValue.Attainment13_Chance))
				setRndReward(ConfigValue.Attainment13_reward);
			else
				_owner.sendMessage("Награда за час - не получена!");
			time=0;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}
		else
		{
			time++;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}
	}
}