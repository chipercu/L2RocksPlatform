package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class Remy extends DefaultAI
{
	static final Location[] points = { new Location(-81926, 243894, -3712), new Location(-82134, 243600, -3728),
			new Location(-83165, 243987, -3728), new Location(-84501, 243245, -3728), new Location(-85100, 243285, -3728),
			new Location(-86152, 242898, -3728), new Location(-86288, 242962, -3720), new Location(-86348, 243223, -3720),
			new Location(-86522, 242762, -3720), new Location(-86500, 242615, -3728), new Location(-86123, 241606, -3728),
			new Location(-85167, 240589, -3728), new Location(-84323, 241245, -3728), new Location(-83215, 241170, -3728),
			new Location(-82364, 242944, -3728), new Location(-81674, 243391, -3712), new Location(-81926, 243894, -3712) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Remy(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_def_think)
		{
			doTask();
			return true;
		}

		if(System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if(!wait)
				switch(current_point)
				{
					case 0:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "A delivery for Mr. Lector? Very good!");
						wait = true;
						return true;
					case 3:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "I need a break!");
						wait = true;
						return true;
					case 7:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "Hello, Mr. Lector! Long time no see, Mr. Jackson!");
						wait = true;
						return true;
					case 12:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "Lulu!");
						wait = true;
						return true;
					case 15:
						wait_timeout = System.currentTimeMillis() + 60000;
						wait = true;
						return true;
				}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if(current_point >= points.length)
				current_point = 0;

			// Remy всегда бегает
			actor.setRunning();

			addTaskMove(points[current_point], true);
			doTask();
			return true;
		}

		if(randomAnimation())
			return true;

		return false;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}