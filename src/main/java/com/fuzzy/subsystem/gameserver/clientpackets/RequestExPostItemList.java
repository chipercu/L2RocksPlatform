package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExReplyPostItemList;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowReceivedPostList;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

/**
 *  Нажатие на кнопку "send mail" в списке из {@link ExShowReceivedPostList}, запрос создания нового письма
 *  В ответ шлется {@link ExReplyPostItemList}
 */
public class RequestExPostItemList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
	//just a trigger
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha != null && (cha.getVar("jailed") != null || cha.is_block || cha.isInEvent() != 0 || !cha.canItemAction()))
			cha.sendPacket(new SystemMessage(SystemMessage.XYOU_DO_NOT_HAVE_XWRITEX_PERMISSION));
		else if(cha != null && cha.getLevel() >= ConfigValue.SendMailLevel)
			cha.sendPacket(new ExReplyPostItemList(cha));
		else if(cha != null)
			cha.sendMessage("Функции отправки почты, доступны с "+ConfigValue.SendMailLevel+" уровня.");
	}
}