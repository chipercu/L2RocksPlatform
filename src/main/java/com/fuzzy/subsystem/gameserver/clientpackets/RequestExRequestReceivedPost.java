package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExReplyReceivedPost;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowReceivedPostList;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 * @see RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		postId = readD(); // id письма
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha != null && (cha.getVar("jailed") != null || cha.is_block || cha.isInEvent() != 0))
			cha.sendPacket(new SystemMessage(SystemMessage.XYOU_DO_NOT_HAVE_XREADX_PERMISSION));
		else if(cha != null)
			cha.sendPacket(new ExReplyReceivedPost(cha, postId));
		//  activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
	}
}