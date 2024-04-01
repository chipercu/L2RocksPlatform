package com.fuzzy.subsystem.gameserver.instancemanager;

import gnu.trove.TIntIntHashMap;
import com.fuzzy.subsystem.common.RunnableImpl;
import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.BlockCheckerEngine;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;
import com.fuzzy.subsystem.gameserver.serverpackets.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HandysBlockCheckerManager
{
	/*
	 * This class manage the player add/remove, team change and
	 * event arena status, as the clearance of the participants
	 * list or liberate the arena
	 */

	// All the participants and their team classifed by arena
	private static ArenaParticipantsHolder[] _arenaPlayers;

	// Arena votes to start the game
	private static TIntIntHashMap _arenaVotes = new TIntIntHashMap();

	// Arena Status, True = is being used, otherwise, False
	private static Map<Integer, Boolean> _arenaStatus;

	// Registration request penalty (10 seconds)
	private static List<Integer> _registrationPenalty = new ArrayList<Integer>();

	/**
	 * Return the number of event-start votes for the spcified
	 * arena id
	 * @param arenaId
	 * @return int (number of votes)
	 */
	public synchronized int getArenaVotes(int arenaId)
	{
		return _arenaVotes.get(arenaId);
	}

	/**
	 * Add a new vote to start the event for the specified
	 * arena id
	 * @param arena
	 */
	public synchronized void increaseArenaVotes(int arena)
	{
		int newVotes = _arenaVotes.get(arena) + 1;
		ArenaParticipantsHolder holder = _arenaPlayers[arena];

		if(newVotes > holder.getPlayers().size() / 2 && !holder.getEvent().isStarted())
		{
			clearArenaVotes(arena);
			if(holder.getBlueTeamSize() == 0 || holder.getRedTeamSize() == 0)
				return;
			if(ConfigValue.HBCEFairPlay)
				holder.checkAndShuffle();
			ThreadPoolManager.getInstance().execute(holder.getEvent().new StartEvent());
		}
		else
			_arenaVotes.put(arena, newVotes);
	}

	/**
	 * Will clear the votes queue (of event start) for the
	 * specified arena id
	 * @param arena
	 */
	public synchronized void clearArenaVotes(int arena)
	{
		_arenaVotes.put(arena, 0);
	}

	private HandysBlockCheckerManager()
	{
		// Initialize arena status
		if(_arenaStatus == null)
		{
			_arenaStatus = new HashMap<Integer, Boolean>();
			_arenaStatus.put(0, false);
			_arenaStatus.put(1, false);
			_arenaStatus.put(2, false);
			_arenaStatus.put(3, false);
		}
	}

	/**
	 * Returns the players holder
	 * @param arena
	 * @return ArenaParticipantsHolder
	 */
	public ArenaParticipantsHolder getHolder(int arena)
	{
		return _arenaPlayers[arena];
	}

	/**
	 * Initializes the participants holder
	 */
	public void startUpParticipantsQueue()
	{
		_arenaPlayers = new ArenaParticipantsHolder[4];

		for(int i = 0; i < 4; ++i)
			_arenaPlayers[i] = new ArenaParticipantsHolder(i);
	}

	/**
	 * Add the player to the specified arena (throught the specified
	 * arena manager) and send the needed server ->  client packets
	 * @param player
	 * @param arenaId
	 */
	public boolean addPlayerToArena(L2Player player, int arenaId)
	{
		ArenaParticipantsHolder holder = _arenaPlayers[arenaId];

		synchronized (holder)
		{
			boolean isRed;

			if(isRegistered(player))
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT).addName(player));
				return false;
			}

			if(player.isCursedWeaponEquipped())
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_REGISTER_WHILE_POSSESSING_A_CURSED_WEAPON));
				return false;
			}

			//KrateisCubeRunnerEvent krateis = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
			//if(krateis.isRegistered(player))
			//{
			//	player.sendPacket(Msg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER);
			//	return false;
			//}

			if(Olympiad.isRegistered(player))
			{
				player.sendPacket(new SystemMessage(SystemMessage.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER));
				return false;
			}
			/*
			if(UnderGroundColiseum.getInstance().isRegisteredPlayer(player))
			{
				UngerGroundColiseum.getInstance().removeParticipant(player);
				player.sendPacket(new SystemMessage(SystemMessageId.COLISEUM_OLYMPIAD_KRATEIS_APPLICANTS_CANNOT_PARTICIPATE));
			}
			 */
			if(_registrationPenalty.contains(player.getObjectId()))
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_MAKE_ANOTHER_REQUEST_FOR_10_SECONDS_AFTER_CANCELLING_A_MATCH_REGISTRATION));
				return false;
			}

			if(holder.getBlueTeamSize() < holder.getRedTeamSize())
			{
				holder.addPlayer(player, 1);
				isRed = false;
			}
			else
			{
				holder.addPlayer(player, 0);
				isRed = true;
			}
			holder.broadCastPacketToTeam(new ExCubeGameAddPlayer(player, isRed));
			return true;
		}
	}

	/**
	 * Will remove the specified player from the specified
	 * team and arena and will send the needed packet to all
	 * his team mates / enemy team mates
	 * @param player
	 * @param arenaId
	 */
	public void removePlayer(L2Player player, int arenaId, int team)
	{
		ArenaParticipantsHolder holder = _arenaPlayers[arenaId];
		synchronized (holder)
		{
			boolean isRed = team == 0;

			holder.removePlayer(player, team);
			holder.broadCastPacketToTeam(new ExCubeGameRemovePlayer(player, isRed));

			// End event if theres an empty team
			int teamSize = isRed ? holder.getRedTeamSize() : holder.getBlueTeamSize();
			if(teamSize == 0)
				holder.getEvent().endEventAbnormally();

			Integer objId = player.getObjectId();
			if(!_registrationPenalty.contains(objId))
				_registrationPenalty.add(objId);
			schedulePenaltyRemoval(objId);
		}
	}

	/**
	 * Will change the player from one team to other (if possible)
	 * and will send the needed packets
	 * @param player
	 * @param arena
	 * @param team
	 */
	public void changePlayerToTeam(L2Player player, int arena, int team)
	{
		ArenaParticipantsHolder holder = _arenaPlayers[arena];

		synchronized (holder)
		{
			boolean isFromRed = holder._redPlayers.contains(player);

			if(isFromRed && holder.getBlueTeamSize() == 6)
			{
				player.sendMessage("The team is full");
				return;
			}
			else if(!isFromRed && holder.getRedTeamSize() == 6)
			{
				player.sendMessage("The team is full");
				return;
			}

			int futureTeam = isFromRed ? 1 : 0;
			holder.addPlayer(player, futureTeam);

			if(isFromRed)
				holder.removePlayer(player, 0);
			else
				holder.removePlayer(player, 1);
			holder.broadCastPacketToTeam(new ExCubeGameChangeTeam(player, isFromRed));
		}
	}

	/**
	 * Will erase all participants from the specified holder
	 * @param arenaId
	 */
	public synchronized void clearPaticipantQueueByArenaId(int arenaId)
	{
		_arenaPlayers[arenaId].clearPlayers();
	}

	public static boolean isRegistered(L2Player player)
	{
		try
		{
			for(int i = 0; i < 4; i++)
				if(_arenaPlayers[i].getPlayers().contains(player))
					return true;
		}
		catch(Exception e)
		{}
		return false;
	}

	/**
	 * Returns true if arena is holding an event at this momment
	 * @param arenaId
	 * @return Boolean
	 */
	public boolean arenaIsBeingUsed(int arenaId)
	{
		if(arenaId < 0 || arenaId > 3)
			return false;
		return _arenaStatus.get(arenaId);
	}

	/**
	 * Set the specified arena as being used
	 * @param arenaId
	 */
	public void setArenaBeingUsed(int arenaId)
	{
		_arenaStatus.put(arenaId, true);
	}

	/**
	 * Set as free the specified arena for future
	 * events
	 * @param arenaId
	 */
	public void setArenaFree(int arenaId)
	{
		_arenaStatus.put(arenaId, false);
	}

	public static HandysBlockCheckerManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		private static HandysBlockCheckerManager _instance = new HandysBlockCheckerManager();
	}

	public class ArenaParticipantsHolder
	{
		int _arena;
		List<L2Player> _redPlayers;
		List<L2Player> _bluePlayers;
		BlockCheckerEngine _engine;

		public ArenaParticipantsHolder(int arena)
		{
			_arena = arena;
			_redPlayers = new ArrayList<L2Player>(6);
			_bluePlayers = new ArrayList<L2Player>(6);
			_engine = new BlockCheckerEngine(this, _arena);
		}

		public List<L2Player> getRedPlayers()
		{
			return _redPlayers;
		}

		public List<L2Player> getBluePlayers()
		{
			return _bluePlayers;
		}

		public ArrayList<L2Player> getPlayers()
		{
			ArrayList<L2Player> all = new ArrayList<L2Player>(12);
			all.addAll(_redPlayers);
			all.addAll(_bluePlayers);
			return all;
		}

		public void addPlayer(L2Player player, int team)
		{
			if(team == 0)
				_redPlayers.add(player);
			else
				_bluePlayers.add(player);
		}

		public void removePlayer(L2Player player, int team)
		{
			if(team == 0)
				_redPlayers.remove(player);
			else
				_bluePlayers.remove(player);
		}

		public int getPlayerTeam(L2Player player)
		{
			if(_redPlayers.contains(player))
				return 0;
			else if(_bluePlayers.contains(player))
				return 1;
			else
				return -1;
		}

		public int getRedTeamSize()
		{
			return _redPlayers.size();
		}

		public int getBlueTeamSize()
		{
			return _bluePlayers.size();
		}

		public void broadCastPacketToTeam(L2GameServerPacket packet)
		{
			ArrayList<L2Player> team = new ArrayList<L2Player>(12);
			team.addAll(_redPlayers);
			team.addAll(_bluePlayers);

			for(L2Player p : team)
				p.sendPacket(packet);
		}

		public void clearPlayers()
		{
			_redPlayers.clear();
			_bluePlayers.clear();
		}

		public BlockCheckerEngine getEvent()
		{
			return _engine;
		}

		public void updateEvent()
		{
			_engine.updatePlayersOnStart(this);
		}

		private void checkAndShuffle()
		{
			int redSize = _redPlayers.size();
			int blueSize = _bluePlayers.size();
			if(redSize > blueSize + 1)
			{
				broadCastPacketToTeam(new SystemMessage(SystemMessage.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
				int needed = redSize - (blueSize + 1);
				for(int i = 0; i < needed + 1; i++)
				{
					L2Player plr = _redPlayers.get(i);
					if(plr == null)
						continue;
					changePlayerToTeam(plr, _arena, 1);
				}
			}
			else if(blueSize > redSize + 1)
			{
				broadCastPacketToTeam(new SystemMessage(SystemMessage.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
				int needed = blueSize - (redSize + 1);
				for(int i = 0; i < needed + 1; i++)
				{
					L2Player plr = _bluePlayers.get(i);
					if(plr == null)
						continue;
					changePlayerToTeam(plr, _arena, 0);
				}
			}
		}
	}

	private void schedulePenaltyRemoval(int objId)
	{
		ThreadPoolManager.getInstance().schedule(new PenaltyRemove(objId), 10000);
	}

	private class PenaltyRemove extends RunnableImpl
	{
		Integer objectId;

		public PenaltyRemove(Integer id)
		{
			objectId = id;
		}

		@Override
		public void runImpl() throws Exception
		{
			_registrationPenalty.remove(objectId);
		}
	}
}