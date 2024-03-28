package com.fuzzy.main.network.session;

import com.fuzzy.main.network.struct.HandshakeData;
import com.fuzzy.main.network.struct.SessionData;

/**
 * Created by Kris on 29.07.2015.
 */
public interface Session {

    String getUuid();

    HandshakeData getHandshakeData();

    SessionData getData();
}
