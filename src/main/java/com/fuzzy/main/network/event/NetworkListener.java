package com.fuzzy.main.network.event;

import com.fuzzy.main.network.session.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Vladimir Ulitin
 * Date: 08.03.12
 * Time: 20:06
 * Слушатель
 */
public interface NetworkListener {

	void onConnect(Session session);

	void onDisconnect(Session session);
}
