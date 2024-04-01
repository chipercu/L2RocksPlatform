package com.fuzzy.subsystem.gameserver.model.base;

import com.fuzzy.subsystem.util.GArray;

public class PlayerAccess
{
	private GArray<String> _notUse = null;
	private GArray<String> _onlyUse = null;

	public int PlayerID;
	public boolean IsGM = false;
	public boolean CanUseGMCommand = false;
	public boolean CanAnnounce = false;

	// Право банить чат
	public boolean CanBanChat = false;
	// Право снимать бан чата
	public boolean CanUnBanChat = false;
	// Право выдавать штрафы
	public boolean CanChatPenalty = false;
	// Задержка в секундах между банами, -1 отключено
	public int BanChatDelay = -1;
	// Максимально разрешенный срок, -1 отключено
	public int BanChatMaxValue = -1;
	// Сколько банов в день разрешено, -1 отключено
	public int BanChatCountPerDay = -1;
	// Выдавать модератору бонус раз в сутки, -1 отключено
	public int BanChatBonusId = -1;
	// Количество бонусов
	public int BanChatBonusCount = -1;

	public boolean CanSetCarma = false;
	public boolean CanCharBan = false;
	public boolean CanCharUnBan = false;
	public boolean CanBan = false;
	public boolean CanUnBan = false;
	public boolean CanTradeBanUnban = false;
	public boolean CanUseBanPanel = false;
	public boolean UseGMShop = false;
	public boolean CanDelete = false;
	public boolean CanKick = false;
	public boolean Menu = false;
	public boolean GodMode = false;
	public boolean CanEditChar = false;
	public boolean CanEditCharAll = false;
	public boolean CanEditPledge = false;
	public boolean CanViewChar = false;
	public boolean CanEditNPC = false;
	public boolean CanViewNPC = false;
	public boolean CanTeleport = false;
	public boolean CanRestart = false;
	public boolean MonsterRace = false;
	public boolean Rider = false;
	public boolean FastUnstuck = false;
	public boolean ResurectFixed = false;
	public boolean Door = false;
	public boolean Res = false;
	public boolean PeaceAttack = false;
	public boolean Heal = false;
	public boolean Unblock = false;
	public boolean AllowWalker = false;
	public boolean UseInventory = true;
	public boolean UseTrade = true;
	public boolean CanAttack = true;
	public boolean CanEvaluate = true;
	public boolean CanJoinParty = true;
	public boolean CanJoinClan = true;
	public boolean UseWarehouse = true;
	public boolean UseShop = true;
	public boolean BlockInventory = false;
	public boolean CanChangeClass = false;
	public boolean CanGmEdit = false;
	public boolean IsEventGm = false;
	public boolean CanReload = false;
	public boolean CanRename = false;
	public boolean CanJail = false;
	public boolean CanPolymorph = false;
	public boolean CanVisBot = false;
	public boolean CanMailReport = false;

	public PlayerAccess()
	{}

	public void setCommand(String command, boolean isNotUse)
	{
		if(isNotUse)
		{
			if(_notUse == null)
				_notUse = new GArray<String>();
			_notUse.add(command);
		}
		else
		{
			if(_onlyUse == null)
				_onlyUse = new GArray<String>();
			_onlyUse.add(command);
		}
	}

	public boolean canUse(String command)
	{
		if(_notUse != null)
			for(String _command : _notUse)
				if(_command.equals(command))
					return false;
		if(_onlyUse != null)
		{
			for(String _command : _onlyUse)
				if(_command.equals(command))
					return true;
			return false;
		}
		return true;
	}
}