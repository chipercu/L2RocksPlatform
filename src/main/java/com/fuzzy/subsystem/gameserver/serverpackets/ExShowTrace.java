package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Location;

public class ExShowTrace extends L2GameServerPacket
{
	private final GArray<Trace> _traces = new GArray<Trace>();
	private int _param = 0;

	static final class Trace
	{
		public final int _x;
		public final int _y;
		public final int _z;

		public Trace(int x, int y, int z)
		{
			_x = x;
			_y = y;
			_z = z;
		}
	}

	public ExShowTrace(int param)
	{
		_param = param;
	}

	public void addTrace(int x, int y, int z)
	{
		_traces.add(new Trace(x, y, z));
	}

	public void addLine(Location from, Location to, int step)
	{
		addLine(from.x, from.y, from.z, to.x, to.y, to.z, step);
	}

	public void addLine(int from_x, int from_y, int from_z, int to_x, int to_y, int to_z, int step)
	{
		int x_diff = to_x - from_x;
		int y_diff = to_y - from_y;
		int z_diff = to_z - from_z;
		double xy_dist = Math.sqrt(x_diff * x_diff + y_diff * y_diff);
		double full_dist = Math.sqrt(xy_dist * xy_dist + z_diff * z_diff);
		int steps = (int) (full_dist / step);

		addTrace(from_x, from_y, from_z);
		if(steps > 1)
		{
			int step_x = x_diff / steps;
			int step_y = y_diff / steps;
			int step_z = z_diff / steps;

			for(int i = 1; i < steps; i++)
				addTrace(from_x + step_x * i, from_y + step_y * i, from_z + step_z * i);
		}
		addTrace(to_x, to_y, to_z);
	}

	public void addTrace(L2Object obj, int time)
	{
		this.addTrace(obj.getX(), obj.getY(), obj.getZ());
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x67);
		writeH(_param);
		writeD(0);
		writeH(_traces.size());
		for(Trace t : _traces)
		{
			writeD(t._x);
			writeD(t._y);
			writeD(t._z);
		}
	}
}