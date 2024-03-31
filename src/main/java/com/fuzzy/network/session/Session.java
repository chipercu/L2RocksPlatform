package com.fuzzy.network.session;

import com.fuzzy.network.struct.HandshakeData;
import com.fuzzy.network.struct.SessionData;

/**
 * Created by Kris on 29.07.2015.
 */
public interface Session {

    String getUuid();

    HandshakeData getHandshakeData();

    SessionData getData();
}
