package com.fuzzy.subsystem.gameserver.model.entity.vehicle;

class L2VehicleArrived extends com.fuzzy.subsystem.common.RunnableImpl
{
	private final L2Vehicle _vehicle;

	public L2VehicleArrived(L2Vehicle vehicle)
	{
		_vehicle = vehicle;
	}

	public void runImpl()
	{
		_vehicle.updatePeopleInTheBoat(_vehicle.getX(), _vehicle.getY(), _vehicle.getZ());
		_vehicle.VehicleArrived();
	}
}