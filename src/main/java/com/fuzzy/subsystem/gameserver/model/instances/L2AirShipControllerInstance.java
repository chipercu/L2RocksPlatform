package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Clan;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2AirShip;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2VehicleManager;
import com.fuzzy.subsystem.gameserver.serverpackets.ExGetOnAirShip;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.tables.AirShipDocksTable;
import com.fuzzy.subsystem.gameserver.tables.AirShipDocksTable.AirShipDock;
import com.fuzzy.subsystem.gameserver.tables.player.PlayerData;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

import java.util.concurrent.ScheduledFuture;

public class L2AirShipControllerInstance extends L2NpcInstance
{
	private static final int ENERGY_STAR_STONE = 13277;
	private static final int AIRSHIP_SUMMON_LICENSE = 13559;
	private ScheduledFuture<?> _autoDepartureTask;
	private static final long AUTO_DEPARTURE_TASK_DELAY = 5 * 60 * 1000L; // 5 min

	public L2AirShipControllerInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("board"))
		{
			SystemMessage msg = canBoard(player);
			if(msg != null)
			{
				player.sendPacket(msg);
				return;
			}

			L2AirShip airship = searchDockedAirShip();
			if(airship == null)
			{
				player.sendActionFailed();
				return;
			}

			if(player.getVehicle() != null && player.getVehicle().getId() == airship.getId())
			{
				player.sendPacket(Msg.YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP);
				return;
			}

			player._stablePoint = player.getLoc().setH(0);
			player.setVehicle(airship);
			player.setInVehiclePosition();
			player.setLoc(airship.getLoc());
			player.broadcastPacket(new ExGetOnAirShip(player, airship, player.getInVehiclePosition()));
		}
		else if(command.equalsIgnoreCase("summon"))
		{
			if(player.getClan() == null || player.getClan().getLevel() < 5)
			{
				player.sendPacket(Msg.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLAN_S_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER);
				return;
			}

			if((player.getClanPrivileges() & L2Clan.CP_CL_SUMMON_AIRSHIP) != L2Clan.CP_CL_SUMMON_AIRSHIP)
			{
				player.sendMessage("You don't have rights to do that.");
				return;
			}

			if(!player.getClan().isHaveAirshipLicense())
			{
				player.sendPacket(Msg.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE);
				return;
			}

			L2AirShip dockedAirship = searchDockedAirShip();
			L2AirShip clanAirship = player.getClan().getAirship();

			if(clanAirship != null)
			{
				if(clanAirship == dockedAirship)
					player.sendPacket(Msg.THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS);
				else
					player.sendPacket(Msg.THE_AIRSHIP_OWNED_BY_THE_CLAN_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER);
				return;
			}

			if(dockedAirship != null)
			{
				player.sendPacket(Msg.ANOTHER_AIRSHIP_HAS_ALREADY_BEEN_SUMMONED_AT_THE_WHARF_PLEASE_TRY_AGAIN_LATER);
				return;
			}

			if(Functions.removeItem(player, ENERGY_STAR_STONE, 5) != 5)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DON_T_HAVE_ENOUGH_S1).addItemName(ENERGY_STAR_STONE));
				return;
			}

			L2AirShip newAirship = new L2AirShip(player.getClan(), "airship", 0);
			AirShipDock ad = AirShipDocksTable.getInstance().getAirShipDockByNpcId(getNpcId());

			L2VehicleManager.getInstance().addStaticItem(newAirship);
			newAirship.SetTrajet1(ad.getArrivalTrajetId(), 0, null, null);
			newAirship.spawn();

			Functions.npcShoutCustomMessage(this, "l2open.gameserver.model.instances.L2AirShipControllerInstance.AirshipSummoned");

			if(_autoDepartureTask != null)
				_autoDepartureTask.cancel(true);
			_autoDepartureTask = ThreadPoolManager.getInstance().schedule(new AutoDepartureTask(newAirship), AUTO_DEPARTURE_TASK_DELAY);
		}
		else if(command.equalsIgnoreCase("register"))
		{
			if(player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5)
			{
				player.sendMessage("You are not allowed to do this.");
				return;
			}

			if(player.getClan().isHaveAirshipLicense())
			{
				player.sendPacket(Msg.THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED);
				return;
			}

			if(Functions.getItemCount(player, AIRSHIP_SUMMON_LICENSE) == 0)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

			Functions.removeItem(player, AIRSHIP_SUMMON_LICENSE, 1);
			player.getClan().setAirshipLicense(true);
			player.getClan().setAirshipFuel(L2AirShip.MAX_FUEL);
			PlayerData.getInstance().updateClanInDB(player.getClan());
			player.sendPacket(Msg.THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED_YOUR_CLAN_CAN_NOW_SUMMON_THE_AIRSHIP);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static SystemMessage canBoard(L2Player player)
	{
		if(player.getTransformation() != 0)
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED;
		if(player.isParalyzed())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED;
		if(player.isDead() || player.isFakeDeath())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD;
		if(player.isFishing())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING;
		if(player.isInCombat())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE;
		if(player.getDuel() != null)
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL;
		if(player.isSitting())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING;
		if(player.isCastingNow())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SKILL_CASTING;
		if(player.isCursedWeaponEquipped())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_CURSED_WEAPON_IS_EQUPPED;
		if(player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG;
		if(player.getPet() != null || player.isMounted())
			return Msg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED;

		return null;
	}

	private L2AirShip searchDockedAirShip()
	{
		L2AirShip airship = null;
		for(L2Character cha : L2World.getAroundCharacters(this, 1000, 500))
			if(cha != null && !cha.isMoving && cha.isAirShip() && L2VehicleManager.getInstance().getBoat(cha.getObjectId()) != null)
			{
				airship = (L2AirShip) cha;
				if(!airship.isDocked())
				{
					airship = null;
					continue;
				}
			}
		return airship;
	}

	private class AutoDepartureTask extends com.fuzzy.subsystem.common.RunnableImpl
	{
		private L2AirShip _airship;

		public AutoDepartureTask(L2AirShip airship)
		{
			_airship = airship;
		}

		public void runImpl()
		{
			if(!_airship.isDocked() || _airship.isMoving)
				return;
			AirShipDock ad = AirShipDocksTable.getInstance().getAirShipDockByNpcId(getNpcId());
			_airship.SetTrajet1(ad.getDepartureTrajetId(), 0, null, null);
			_airship._cycle = 1;
			_airship.begin();
		}
	}
}