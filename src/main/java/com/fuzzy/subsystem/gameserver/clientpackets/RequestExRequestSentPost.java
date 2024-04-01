package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExReplySentPost;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowSentPostList;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}.
 * В ответ шлется {@link ExReplySentPost}.
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket
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
		if(cha != null)
			cha.sendPacket(new ExReplySentPost(cha, postId));
	}
}