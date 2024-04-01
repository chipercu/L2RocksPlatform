package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.clientpackets.RequestExCancelSentPost;
import com.fuzzy.subsystem.gameserver.clientpackets.RequestExRequestSentPost;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.TradeItem;
import com.fuzzy.subsystem.gameserver.model.items.MailParcelController;
import com.fuzzy.subsystem.gameserver.model.items.MailParcelController.Letter;

/**
 * Просмотр собственного отправленного письма. Шлется в ответ на {@link RequestExRequestSentPost}.
 * При нажатии на кнопку Cancel клиент шлет {@link RequestExCancelSentPost}.
 * @see ExReplyReceivedPost
 */
public class ExReplySentPost extends L2GameServerPacket
{
	private Letter _letter;

	public ExReplySentPost(L2Player cha, int post)
	{
		_letter = MailParcelController.getInstance().getLetter(post);

		if(_letter == null)
		{
			_letter = new Letter();
			cha.sendPacket(new ExShowSentPostList(cha));
		}
	}

	// ddSSS dx[hddQdddhhhhhhhhhh] Qd
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xAD);

		if(getClient().isLindvior())
			writeD(0x00); // writeD(mail.getType().ordinal());

		writeD(_letter.id); // id письма
		writeD(_letter.price > 0 ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		writeS(_letter.receiverName); // кому
		writeS(_letter.topic); // топик
		writeS(_letter.body); // тело

		writeD(_letter.attached.size()); // количество приложенных вещей
		for(TradeItem temp : _letter.attached)
		{
			writeItemInfo(temp);
			writeD(temp.getObjectId());
		}

		writeQ(_letter.price); // для писем с оплатой - цена
		if(getClient().isLindvior())
			writeD(0x00);
		writeD(_letter.attachments > 0 ? 1 : 0);
        writeD(_letter.system);
	}
}