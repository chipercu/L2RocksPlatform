package com.fuzzy.network.transport;

import com.fuzzy.network.transport.Transport;

/**
 * Created by kris on 26.08.16.
 */
public interface TransportListener {

    public void onConnect(com.fuzzy.network.transport.Transport transport, Object channel, String remoteIpAddress);

    public void incomingMessage(com.fuzzy.network.transport.Transport transport, Object channel, String message);

    public void onDisconnect(Transport transport, Object channel, int statusCode, Throwable throwable);
}
