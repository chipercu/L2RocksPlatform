package com.fuzzy.subsystem.gameserver.model;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.extensions.listeners.MethodCollection;
import com.fuzzy.subsystem.extensions.listeners.engine.DefaultListenerEngine;
import com.fuzzy.subsystem.extensions.listeners.engine.ListenerEngine;
import com.fuzzy.subsystem.extensions.listeners.events.L2Zone.L2ZoneEnterLeaveEvent;
import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncAdd;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncMul;
import com.fuzzy.subsystem.gameserver.tables.SkillTable;
import com.fuzzy.subsystem.gameserver.taskmanager.LazyPrecisionTaskManager;
import com.fuzzy.subsystem.util.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class L2Zone
{
	private static Logger _log = Logger.getLogger(L2Zone.class.getName());

	public static enum ZoneType
	{
		Town,
		Castle,
		ClanHall,
		Fortress,
		OlympiadStadia,
		MonsterDerbyTrack,
		CastleDefenderSpawn,
		fishing,
		Underground,
		Siege,
		water,
		battle_zone,
		damage,
		instant_skill,
		mother_tree,
		peace_zone,
		poison,
		ssq_zone,
		swamp,
		no_escape,
		no_landing,
		no_restart,
		no_spawn,
		siege_residense, // территория замка, ограничена забором
		RESIDENCE,

		dummy,
		offshore,
		unblock_actions,
		no_water,
		epic,
		monster_trap,
		no_summon,
		UnderGroundColiseum,
		set_fame,
		other,
		zone_check_bot,
		zone_chat_limit,
		zone_save_tp,
		event,
		allow_forbidden,
		allow_outpost,
		block_outpost;
	}

	int _id;
	ZoneType _type;
	private String _name;
	private L2Territory _loc;
	private L2Territory _restartPoints;
	private L2Territory _PKrestartPoints;
	private int _entering_message_no;
	private int _leaving_message_no;
	private boolean _active;
	public L2Skill _skill;
	int _skillProb;
	long _unitTick;
	private long _initialDelay;
	public int reflection = -1;
	private ZoneTarget _target;
	private int _index;
	private int _taxById;
	private long _restartTime;
	private String _blockedActions;
	private Location _teleportEnter = null;
	private Location _teleportExit = null;
	public static final String BLOCKED_ACTION_PRIVATE_STORE = "private store";
	public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "private workshop";

	private String _event;
	private int _reflectionId;
	public boolean instance_only=false;

	private ListenerEngine<L2Zone> listenerEngine;
	private DamageTask damageTask;
	private final GArray<L2Object> _objects = new GArray<L2Object>(0);
	private final Map<L2Character, ZoneSkillTimer> _skillTimers = new ConcurrentHashMap<L2Character, ZoneSkillTimer>(0);

	/**
	 * Ордер в зонах, с ним мы и добавляем/убираем статы.
	 * TODO: сравнить ордер с оффом, пока от фонаря
	 */
	public final static int ZONE_STATS_ORDER = 0x40;

	/**
	 * Сообщение которое шлется при уроне от зоны (не скилла)
	 * К примеру на осадах. Пока это только 686 (You have received $s1 damage from the fire of magic.)
	 */
	private int messageNumber;

	/**
	 * Урон от зоны по хп
	 */
	private int damageOnHP;

	/**
	 * Урон от зоны по мп
	 */
	private int damageOnMP;

	/**
	 * Бонус/штраф к скорости движения
	 */
	private int moveBonus;

	/**
	 * Бонус регенерации хп
	 */
	private int regenBonusHP;

	/**
	 * Бонус регенерации мп
	 */
	private int regenBonusMP;

	/**
	 * Бонус регенерации цп
	 */
	private int regenBonusCP;

	private float mDefBonus=0;
	private float pDefBonus=0;

	/**
	 * Раса на которую применим эффект
	 */
	private int affectTeam=0;
	private String affectRace;

	public L2Zone(int id)
	{
		_id = id;
	}

	public final int getId()
	{
		return _id;
	}

	@Override
	public final String toString()
	{
		return "zone '" + _id + "'";
	}

	public ZoneType getType()
	{
		return _type;
	}

	public void setType(ZoneType type)
	{
		_type = type;
	}

	public void setLoc(L2Territory t)
	{
		_loc = t;
	}

	public void setTeleportEnter(int x, int y, int z)
	{
		_teleportEnter = new Location(x, y, z);
	}

	public void setTeleportExit(int x, int y, int z)
	{
		_teleportExit = new Location(x, y, z);
	}

	public void setRestartPoints(L2Territory t)
	{
		_restartPoints = t;
	}

	public void setPKRestartPoints(L2Territory t)
	{
		_PKrestartPoints = t;
	}

	public L2Territory getLoc()
	{
		return _loc;
	}

	public L2Territory getRestartPoints()
	{
		return _restartPoints;
	}

	public L2Territory getPKRestartPoints()
	{
		return _PKrestartPoints;
	}

	public Location getSpawn()
	{
		// При ошибке портанет во флоран
		if(_restartPoints == null)
		{
			_log.warning("L2Zone.getSpawn(): restartPoint not found for " + toString());
			return new Location(17817, 170079, -3530);
		}
		GArray<int[]> coords = _restartPoints.getCoords();
		int[] coord = coords.get(Rnd.get(coords.size()));
		return new Location(coord);
	}

	public GArray<int[]> getSpawns()
	{
		return _restartPoints.getCoords();
	}

	public Location getPKSpawn()
	{
		if(_PKrestartPoints == null)
			return getSpawn();
		GArray<int[]> coords = _PKrestartPoints.getCoords();
		int rnd = Rnd.get(coords.size());
		int[] coord = coords.get(rnd);
		return new Location(coord);
	}

	/**
	 * Проверяет находятся ли даные координаты в зоне.
	 * _loc - стандартная территория для зоны
	 * @param x координата
	 * @param y координата
	 * @return находятся ли координаты в локации
	 */
	public boolean checkIfInZone(int x, int y)
	{
		return _loc != null && _loc.isInside(x, y);
	}

	public boolean checkIfInZone(int x, int y, int z)
	{
		return _loc != null && _loc.isInside(x, y, z);
	}

	public boolean checkIfInZone(L2Object obj)
	{
		return _loc != null && _loc.isInside(obj.getX(), obj.getY(), obj.getZ()) && (reflection == -1 || reflection == obj.getReflectionId() || reflection == 111 && obj.getReflectionId() > 0);
	}

	public final double findDistanceToZone(L2Object obj, boolean includeZAxis)
	{
		return findDistanceToZone(obj.getX(), obj.getY(), obj.getZ(), includeZAxis);
	}

	public final double findDistanceToZone(int x, int y, int z, boolean includeZAxis)
	{
		double closestDistance = 99999999;
		if(_loc == null)
			return closestDistance;
		double distance;
		for(int[] coord : _loc.getCoords())
		{
			distance = Util.calculateDistance(x, y, z, coord[0], coord[1], (coord[2] + coord[3]) / 2, includeZAxis);
			if(distance < closestDistance)
				closestDistance = distance;
		}
		return closestDistance;
	}

	public GArray<int[]> getCoords()
	{
		return _loc.getCoords();
	}

	/**
	 * Обработка входа в территорию
	 * Ojbect всегда добавляется в список вне зависимости от активности территории.
	 * Если зона акивная, то обработается вход в зону
	 * @param object кто входит
	 */
	public void doEnter(L2Object object)
	{
		//if(object.isPlayer())
		//	_log.info("L2Zone: doEnter["+object.getName()+"]["+this+"]["+instance_only+"]["+(reflection == object.getReflectionId())+"("+reflection+"|"+object.getReflectionId()+")]");
		synchronized (_objects)
		{
			if(!_objects.contains(object))
				_objects.add(object);
			else
			{
				_log.severe("Attempt of double object add to zone " + _name + " id " + _id);
				Util.test();
			}
		}

		if(!isActive())
			return;

		onZoneEnter(object);
	}

	/**
	 * Обработка выхода из зоны
	 * Object всегда убирается со списка вне зависимости от зоны
	 * Если зона активная, то обработается выход из зоны
	 * @param object кто выходит
	 */
	public void doLeave(L2Object object, boolean notify)
	{
		//if(object.isPlayer())
		//	_log.info("L2Zone: doLeave["+object.getName()+"]["+this+"]["+instance_only+"]["+(reflection == object.getReflectionId())+"("+reflection+"|"+object.getReflectionId()+")]");
		boolean remove = false;
		synchronized (_objects)
		{
			remove = _objects.remove(object);
			if(!remove)
			{
				_log.severe("Attempt remove object from zone " + _name + " id " + _id + " where it's absent");
				Util.test();
			}
		}

		if(!isActive())
			return;

		if(notify && remove)
			onZoneLeave(object);
	}

	/**
	 * Обработка входа в зону
	 * @param object кто входит
	 */
	private void onZoneEnter(L2Object object)
	{
		if(object.inObserverMode())
			return;

		//if(object.isPlayer())
		//	_log.info("L2Zone: onZoneEnter["+object.getName()+"]["+this+"]["+instance_only+"]["+(reflection == object.getReflectionId())+"("+reflection+"|"+object.getReflectionId()+")]");

		object.addZone(this);

		getListenerEngine().fireMethodInvoked(new L2ZoneEnterLeaveEvent(MethodCollection.L2ZoneObjectEnter, this, new L2Object[] { object }));

		checkEffects(object, true);

		addZoneStats((L2Character) object);

		if(object.isPlayer())
			((L2Player) object).doZoneCheck(_entering_message_no);
	}

	/**
	 * Обработка выхода из зоны
	 * @param object кто выходит
	 */
	private void onZoneLeave(L2Object object)
	{
		if(object.inObserverMode())
			return;

		/*if(object.isPlayer())
		{
			_log.info("L2Zone: onZoneLeave["+object.getName()+"]["+this+"]["+instance_only+"]["+(reflection == object.getReflectionId())+"("+reflection+"|"+object.getReflectionId()+")]");
			if(_id == 7030 || _id == 7031)
				Util.test();
		}*/

		object.removeZone(this);

		getListenerEngine().fireMethodInvoked(new L2ZoneEnterLeaveEvent(MethodCollection.L2ZoneObjectLeave, this, new L2Object[] { object }));

		checkEffects(object, false);

		removeZoneStats((L2Character) object);

		if(object.isPlayer())
			((L2Player) object).doZoneCheck(_leaving_message_no);
	}

	/**
	 * Добавляет статы зоне
	 * @param object обьект которому добавляется
	 */
	private void addZoneStats(L2Character object)
	{
		// Проверка цели
		if(_target != null)
			if(!checkTarget(object))
				return;

		// Скорость движения накладывается только на L2Playable
		// affectRace в базе не указан, если надо будет влияние, то поправим
		if(object.isPlayable())
		{
			if(getMoveBonus() != 0)
				object.addStatFunc(new FuncAdd(Stats.p_speed, ZONE_STATS_ORDER, this, getMoveBonus()));

			if(getMDefBonus() != 0)
				object.addStatFunc(new FuncMul(Stats.p_magical_defence, 0x30, this, getMDefBonus()));

			if(getPDefBonus() != 0)
				object.addStatFunc(new FuncMul(Stats.p_physical_defence, 0x30, this, getPDefBonus()));

			if(getMoveBonus() > 0 || getMDefBonus() > 0 || getPDefBonus() > 0)
				object.sendChanges();
		}

		if(affectTeam > 0 && affectTeam != object.getTeam())
			return;

		// Если раса указана, то это уже не NPC
		if(affectRace != null && !object.isPlayer())
			return;

		// Если у нас раса не "all"
		if(affectRace != null && !affectRace.equalsIgnoreCase("all"))
		{
			L2Player player = object.getPlayer();

			// если раса не подходит
			if(!player.getRace().toString().equalsIgnoreCase(affectRace))
				return;
		}

		// Если у нас есть что регенить
		if(getRegenBonusHP() != 0)
			object.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, ZONE_STATS_ORDER, this, getRegenBonusHP()));

		// Если у нас есть что регенить
		if(getRegenBonusMP() != 0)
			object.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, ZONE_STATS_ORDER, this, getRegenBonusMP()));

		// Если у нас есть что регенить
		if(getRegenBonusCP() != 0)
			object.addStatFunc(new FuncAdd(Stats.REGENERATE_CP_RATE, ZONE_STATS_ORDER, this, getRegenBonusCP()));
	}

	/**
	 * Убирает добавленые зоной статы
	 * @param object обьект у которого убирается
	 */
	private void removeZoneStats(L2Character object)
	{
		if(getRegenBonusHP() == 0 && getRegenBonusMP() == 0 && getRegenBonusCP() == 0 && getMoveBonus() == 0 && getMDefBonus() == 0 && getPDefBonus() == 0)
			return;

		object.removeStatsOwner(this);

		if(getMoveBonus() > 0 || getMDefBonus() > 0 || getPDefBonus() > 0)
			object.sendChanges();
	}

	/**
	 * Применяет эффекты при входе/выходе из(в) зону
	 * @param object обьект
	 * @param enter вошел или вышел
	 */
	private void checkEffects(L2Object object, boolean enter)
	{
		if(_event != null && _event.startsWith("script"))
		{
			String[] param = _event.split(";");
			Functions.callScripts(param[1], param[2], new Object[] { this, object, enter });
		}

		//if(getId() == 60009 || getId() == 60010 || getId() == 60011)
		//	_log.info("checkEffects("+getId()+"): object="+object+" enter="+enter+" isNpc="+object.isNpc()+" checkTarget="+checkTarget(object));
		if(enter)
		{
			if(_teleportEnter != null && object.isPlayable())
				((L2Character) object).teleToLocation(_teleportEnter);
		}
		else if(_teleportExit != null && object.isPlayable())
			((L2Character) object).teleToLocation(_teleportExit);

		if(_skill == null || _target == null || !checkTarget(object))
			return;

		L2Character p = (L2Character) object;
		if(enter)
		{
			ZoneSkillTimer t = new ZoneSkillTimer(p);
			_skillTimers.put(p, t);
			if(_initialDelay > 0)
			{
				if(ConfigValue.Dev_ZoneLazyTaskUse)
					LazyPrecisionTaskManager.getInstance().schedule(t, _initialDelay);
				else
					ThreadPoolManager.getInstance().schedule(t, _initialDelay);
			}
			else
				t.run();
		}
		else
		{
			ZoneSkillTimer rem = _skillTimers.remove(p);
			if(rem != null)
				rem.setCanceled(true);
		}
	}

	/**
	 * Проверяет подходит ли обьект для вызвавшего действия
	 * @param object обьект
	 * @return подошел ли
	 */
	private boolean checkTarget(L2Object object)
	{
		switch(_target)
		{
			case pc:
				return object.isPlayable();
			case only_pc:
				return object.isPlayer();
			case npc:
				return object.isNpc();
		}
		return false;
	}

	public L2Object[] getObjects()
	{
		synchronized (_objects)
		{
			return _objects.toArray(new L2Object[_objects.size()]);
		}
	}

	public GArray<L2Object> getInsideObjectsIncludeZ()
	{
		L2Object[] all_objects = getObjects();
		GArray<L2Object> result = new GArray<L2Object>();
		for(L2Object obj : all_objects)
			if(obj != null && _loc.isInside(obj))
				result.add(obj);
		return result;
	}

	public GArray<L2Player> getInsidePlayers()
	{
		L2Object[] all_objects = getObjects();
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2Object obj : all_objects)
			if(obj != null && obj.isPlayer())
				result.add((L2Player) obj);
		return result;
	}

	public GArray<L2Player> getInsidePlayersIncludeZ()
	{
		L2Object[] all_objects = getObjects();
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2Object obj : all_objects)
			if(obj != null && obj.isPlayer() && _loc.isInside(obj))
				result.add((L2Player) obj);
		return result;
	}

	/**
	 * Установка активности зоны
	 * @param value активна ли зона
	 */
	public void setActive(boolean value)
	{
		if(_active == value)
			return;
		
		synchronized (_objects)
		{
			_active = value;

			for(L2Object obj : _objects)
				if((reflection == -1 || reflection == obj.getReflectionId() || reflection == 111 && obj.getReflectionId() > 0 || !value))
				{
					if(_active)
						onZoneEnter(obj);
					else
						onZoneLeave(obj);
					//obj.updateTerritories();
				}
		}

		setDamageTaskActive(value);
	}

	/**
	 * Запускает или останавливает таск урона в зоне.<br>
	 * <b>Таск <font color="red">не</font> запустится если урон по хп и мп равны 0</b>
	 * @param value запустить или остановить таск урона
	 */
	public void setDamageTaskActive(boolean value)
	{
		if(value && (getDamageOnHP() > 0 || getDamageOnMP() > 0))
			new DamageTask().start();
		else
			damageTask = null;
	}

	public boolean isActive()
	{
		return _active;
	}

	public final String getName()
	{
		return _name;
	}

	public final void setName(String name)
	{
		_name = name;
	}

	public final int getEnteringMessageId()
	{
		return _entering_message_no;
	}

	public final void setEnteringMessageId(int id)
	{
		_entering_message_no = id;
	}

	public final int getLeavingMessageId()
	{
		return _leaving_message_no;
	}

	public final void setLeavingMessageId(int id)
	{
		_leaving_message_no = id;
	}

	public final void setIndex(int index)
	{
		_index = index;
	}

	public final int getIndex()
	{
		return _index;
	}

	public final void setTaxById(int id)
	{
		_taxById = id;
	}

	public final Integer getTaxById()
	{
		return _taxById;
	}

	private int[] _hour_of_day;
	public boolean _batle=true;
	public int _no_attack_time=0;
	public ZoneItemInfo[] item_reward = null;

	public void setHourOfDay(int[] hour_of_day)
	{
		_hour_of_day = hour_of_day;
	}

	public int[] getHourOfDay()
	{
		return _hour_of_day;
	}

	public void setAddItem(int item_id, long item_count_min, long item_count_max, double chance, boolean hwid, boolean ip)
	{
		item_reward = ArrayUtils.add(item_reward, new ZoneItemInfo(item_id, item_count_min, item_count_max, chance, hwid, ip));
	}

	public void setSkill(String skill)
	{
		if(skill == null || skill.equalsIgnoreCase("null"))
			return;
		String[] sk = skill.split(";");
		setSkill(SkillTable.getInstance().getInfo(Short.parseShort(sk[0]), Short.parseShort(sk[1])));
	}

	public void setSkill(L2Skill skill)
	{
		_skill = skill;
	}


	public void deleteSkill()
	{
		_skill = null;	
	}

	public void setSkillProb(String chance)
	{
		if(chance == null)
		{
			_skillProb = -1;
			return;
		}
		_skillProb = Integer.parseInt(chance);
	}

	public void setUnitTick(String delay)
	{
		if(delay == null)
		{
			_unitTick = 666;
			return;
		}

		_unitTick = Integer.parseInt(delay) * 666;
	}

	public void setInitialDelay(String delay)
	{
		if(delay == null)
		{
			_initialDelay = 0;
			return;
		}

		_initialDelay = Integer.parseInt(delay) * 1000L;
	}

	public void setTarget(String target)
	{
		if(target == null)
			return;

		_target = ZoneTarget.valueOf(target);
	}

	public enum ZoneTarget
	{
		pc,
		npc,
		only_pc
	}

	public long getRestartTime()
	{
		return _restartTime;
	}

	public ZoneTarget getZoneTarget()
	{
		return _target;
	}

	public void setRestartTime(long restartTime)
	{
		_restartTime = restartTime;
	}

	public void setBlockedActions(String blockedActions)
	{
		_blockedActions = blockedActions;
	}

	public boolean isActionBlocked(String action)
	{
		return _blockedActions != null && _blockedActions.contains(action);
	}

	public ListenerEngine<L2Zone> getListenerEngine()
	{
		if(listenerEngine == null)
			listenerEngine = new DefaultListenerEngine<L2Zone>(this);
		return listenerEngine;
	}

	/**
	 * Номер системного вообщения которое будет отослано игроку при нанесении урона зоной
	 * @return SystemMessage ID
	 */
	public int getMessageNumber()
	{
		return messageNumber;
	}

	/**
	 * Устанавлевает номер системного сообщение которое будет отослано игроку при уроне зоной
	 * @param messageNumber SystemMessage ID
	 */
	public void setMessageNumber(int messageNumber)
	{
		this.messageNumber = messageNumber;
	}

	/**
	 * Устанавлевает номер системного сообщение которое будет отослано игроку при уроне зоной
	 * @param number SystemMessage ID - Системное сообщение сторой
	 */
	public void setMessageNumber(String number)
	{
		setMessageNumber(number == null ? 0 : Integer.parseInt(number));
	}

	/**
	 * Сколько урона зона нанесет по хп
	 * @return количество урона
	 */
	public int getDamageOnHP()
	{
		return damageOnHP;
	}

	/**
	 * Установка урона по хп зоной
	 * @param damageOnHP сколько зона нанесет урона по хп
	 */
	public void setDamageOnHP(int damageOnHP)
	{
		this.damageOnHP = damageOnHP;
	}

	/**
	 * Установка урона по хп зоной. Парсит строку.
	 * @param damage урон в строке
	 */
	public void setDamageOnHP(String damage)
	{
		setDamageOnHP(damage == null ? 0 : Integer.parseInt(damage));
	}

	/**
	 * Сколько урона зона нанесет по мп
	 * @return количество урона
	 */
	public int getDamageOnMP()
	{
		return damageOnMP;
	}

	/**
	 * Установка урона по мп зоной
	 * @param damageOnMP сколько зона нанесет урона по мп
	 */
	public void setDamageOnMP(int damageOnMP)
	{
		this.damageOnMP = damageOnMP;
	}

	/**
	 * Установка урона по мп зоной. Парсит строку.
	 * @param damage урон в строке
	 */
	public void setDamageOnМP(String damage)
	{
		setDamageOnMP(damage == null ? 0 : Integer.parseInt(damage));
	}

	/**
	 * @return Бонус к скорости движения в зоне
	 */
	public int getMoveBonus()
	{
		return moveBonus;
	}

	/**
	 * Устанавливает бонус на движение в зоне
	 * @param moveBonus бонус
	 */
	public void setMoveBonus(int moveBonus)
	{
		this.moveBonus = moveBonus;
	}

	/**
	 * Устанавливает бонус на движение в зоне
	 * @param moveBonus бонус
	 */
	public void setMoveBonus(String moveBonus)
	{
		setMoveBonus(moveBonus == null ? 0 : Integer.parseInt(moveBonus));
	}

	/**
	 * Возвращает бонус регенерации хп в этой зоне
	 * @return Бонус регенарации хп в этой зоне
	 */
	public int getRegenBonusHP()
	{
		return regenBonusHP;
	}

	/**
	 * Устанавливает бонус регена хп в этой зоне. Парсит строку
	 * @param bonus бонус регена
	 */
	public void setRegenBonusHP(String bonus)
	{
		setRegenBonusHP(bonus == null ? 0 : Integer.parseInt(bonus));
	}

	/**
	 * Устанавливает бонус регенерации хп в этой зоне
	 * @param regenBonusHP бонус регена ХП
	 */
	public void setRegenBonusHP(int regenBonusHP)
	{
		this.regenBonusHP = regenBonusHP;
	}

	/**
	 * Возвращает бонус регенерации мп в этой зоне
	 * @return Бонус регенарации мп в этой зоне
	 */
	public int getRegenBonusMP()
	{
		return regenBonusMP;
	}

	/**
	 * Устанавливает бонус регенерации хп в этой зоне
	 * @param regenBonusMP бонус регена МП
	 */
	public void setRegenBonusMP(int regenBonusMP)
	{
		this.regenBonusMP = regenBonusMP;
	}

	/**
	 * Устанавливает бонус регена мп в этой зоне. Парсит строку
	 * @param bonus бонус регена
	 */
	public void setRegenBonusMP(String bonus)
	{
		setRegenBonusMP(bonus == null ? 0 : Integer.parseInt(bonus));
	}

	/**
	 * Возвращает бонус регенерации цп в этой зоне
	 * @return Бонус регенарации цп в этой зоне
	 */
	public int getRegenBonusCP()
	{
		return regenBonusCP;
	}

	/**
	 * Устанавливает бонус регенерации цп в этой зоне
	 * @param regenBonusCP бонус регена ЦП
	 */
	public void setRegenBonusCP(int regenBonusCP)
	{
		this.regenBonusCP = regenBonusCP;
	}

	/**
	 * Устанавливает бонус регена цп в этой зоне. Парсит строку
	 * @param bonus бонус регена
	 */
	public void setRegenBonusCP(String bonus)
	{
		setRegenBonusMP(bonus == null ? 0 : Integer.parseInt(bonus));
	}
	
	// -----
	public float getMDefBonus()
	{
		return mDefBonus;
	}

	public void setMDefBonus(float mDefBonus)
	{
		this.mDefBonus = mDefBonus;
	}

	public void setMDefBonus(String bonus)
	{
		setMDefBonus(bonus == null ? 0 : Float.parseFloat(bonus));
	}

	public float getPDefBonus()
	{
		return pDefBonus;
	}

	public void setPDefBonus(float pDefBonus)
	{
		this.pDefBonus = pDefBonus;
	}

	public void setPDefBonus(String bonus)
	{
		setPDefBonus(bonus == null ? 0 : Float.parseFloat(bonus));
	}
	// -----

	/**
	 * Возвращает расу на которую примени эффект. Раса может быть:
	 * <br><b>all</b> - любая раса
	 * <br><b>null</b> - проверка не применяется
	 * ну или любая из энумы Race
	 * @see l2open.gameserver.model.base.Race
	 * @return раса
	 */
	public String getAffectRace()
	{
		return affectRace;
	}

	/**
	 * Устанавливает расу зоне на которую будут применятся эффекты. Раса может быть:
	 * <br><b>all</b> - любая раса
	 * <br><b>null</b> - проверка не применяется
	 * ну или любая из энумы Race
	 * @see l2open.gameserver.model.base.Race
	 * @param affectRace строка с расой
	 */
	public void setAffectRace(String affectRace)
	{
		if(affectRace != null)
			affectRace = affectRace.toLowerCase().replace(" ", "");

		this.affectRace = affectRace;
	}

	public void setTeam(String value)
	{
		affectTeam = (value == null ? 0 : Integer.parseInt(value));
	}

	public String getEvent()
	{
		return _event;
	}

	public void setEvent(String event)
	{
		_event = event;
	}

	public void setReflectionId(int val)
	{
		_reflectionId = val;
	}

	public int getReflectionId()
	{
		return _reflectionId;
	}

	public void setParam(String name, String value)
	{
	// TODO: implement
	}

	private class ZoneSkillTimer extends com.fuzzy.subsystem.common.RunnableImpl
	{
		private boolean canceled = false;
		private L2Character _target;

		public ZoneSkillTimer(L2Character target)
		{
			_target = target;
		}

		public void runImpl()
		{
			if(canceled)
				return;

			if((_skillProb == 100 || Rnd.chance(_skillProb)) && !_target.isDead())
				_skill.getEffects(_target, _target, false, false);

			if(ConfigValue.Dev_ZoneLazyTaskUse)
				LazyPrecisionTaskManager.getInstance().schedule(this, _unitTick);
			else
				ThreadPoolManager.getInstance().schedule(this, _unitTick);
		}

		public void setCanceled(boolean val)
		{
			canceled = val;
		}
	}
	/*private abstract class Timer extends com.fuzzy.subsystem.common.RunnableImpl
	{
		protected L2Character _target;
		protected Future<?> _future;
		protected boolean _active;

		public Timer(L2Character cha)
		{
			_target = cha;
		}

		public void start()
		{
			_active = true;
			_future = LazyPrecisionTaskManager.getInstance().schedule(this, getTemplate().getInitialDelay() * 1000L);
		}

		public void stop()
		{
			_active = false;
			if(_future != null)
			{
				_future.cancel(false);
				_future = null;
			}
		}

		public void next()
		{
			if(!_active)
				return;
			if(_unitTick == 0)
				return;
			_future = LazyPrecisionTaskManager.getInstance().schedule(this, _unitTick);
		}

		@Override
		public abstract void runImpl() throws Exception;
	}

	private class ZoneSkillTimer extends Timer
	{
		public ZoneSkillTimer(L2Character cha)
		{
			super(cha);
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isActive())
				return;

			if((_skillProb == 100 || Rnd.chance(_skillProb)) && !_target.isDead())
				_skill.getEffects(_target, _target, false, false);

			next();
		}
	}

	private class DamageTimer extends Timer
	{
		public DamageTimer(L2Character cha)
		{
			super(cha);
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isActive())
				return;

					if(_target != null && !checkTarget(object)) // Если явно указана цель
						continue;

					if(_target == null && !object.isPlayable()) // Если цель не указана, то валим только игроков
						continue;

					L2Character target = (L2Character) object;
					if(target.isDead())
						continue;

					int hp = getDamageOnHP();
					int mp = getDamageOnMP();
					int message = getMessageNumber();

					if(hp > 0)
					{
						target.reduceCurrentHp(hp, target, null, false, true, true, false, false, hp, true, false, false, false);
						if(message > 0)
							target.sendPacket(new SystemMessage(message).addNumber(hp));
					}

					if(mp > 0)
					{
						target.reduceCurrentMp(mp, null);
						if(message > 0)
							target.sendPacket(new SystemMessage(message).addNumber(mp));
					}

			next();
		}
	}*/

	/**
	 * Класс используется для нанесения урона всем кто в зоне.
	 */
	private class DamageTask extends com.fuzzy.subsystem.common.RunnableImpl
	{
		public void runImpl()
		{
			if(damageTask != this || !isActive())
				return;

			synchronized (_objects)
			{
				for(L2Object object : _objects)
				{
					if(_target != null && !checkTarget(object)) // Если явно указана цель
						continue;

					if(_target == null && !object.isPlayable()) // Если цель не указана, то валим только игроков
						continue;

					L2Character target = (L2Character) object;
					if(target.isDead())
						continue;

					int hp = getDamageOnHP();
					int mp = getDamageOnMP();
					int message = getMessageNumber();

					if(hp > 0)
					{
						target.reduceCurrentHp(hp, target, null, false, true, true, false, false, hp, true, false, false, false);
						if(message > 0)
							target.sendPacket(new SystemMessage(message).addNumber(hp));
					}

					if(mp > 0)
					{
						target.reduceCurrentMp(mp, null);
						if(message > 0)
							target.sendPacket(new SystemMessage(message).addNumber(mp));
					}
				}
			}

			if(ConfigValue.Dev_ZoneLazyTaskUse)
				LazyPrecisionTaskManager.getInstance().schedule(this, 1000);
			else
				ThreadPoolManager.getInstance().schedule(this, 1000);
		}

		/**
		 * Запускает таймер нанесения урона зоной
		 */
		public void start()
		{
			damageTask = this;

			if(_initialDelay == 0)
			{
				run();
				return;
			}

			if(ConfigValue.Dev_ZoneLazyTaskUse)
				LazyPrecisionTaskManager.getInstance().schedule(this, _initialDelay);
			else
				ThreadPoolManager.getInstance().schedule(this, _initialDelay);
		}
	}

	public static class ZoneItemInfo
	{
		int item_id;
		long item_count_min;
		long item_count_max;
		double chance;
		boolean hwid;
		boolean ip;

		public ZoneItemInfo(int _item_id, long _item_count_min, long _item_count_max, double _chance, boolean _hwid, boolean _ip)
		{
			item_id = _item_id;
			item_count_min = _item_count_min;
			item_count_max = _item_count_max;
			chance = _chance;
			hwid = _hwid;
			ip = _ip;	
		}
	}
}