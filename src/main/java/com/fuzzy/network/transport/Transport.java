package com.fuzzy.network.transport;

import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.struct.info.TransportInfo;
import com.fuzzy.network.transport.TransportListener;
import com.fuzzy.network.transport.TypeTransport;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

/**
 * Created by kris on 26.08.16.
 */
public abstract class Transport<Channel> {

    private final Set<TransportListener> listeners;

    public Transport() {
        this.listeners = new CopyOnWriteArraySet<TransportListener>();
    }

    public void addTransportListener(TransportListener listener) {
        this.listeners.add(listener);
    }

    public void removeTransportListener(TransportListener listener) {
        this.listeners.remove(listener);
    }

    public abstract TypeTransport getType();

    public void fireConnect(Channel channel, String remoteIpAddress) {
        for (TransportListener transportListener: this.listeners) {
            transportListener.onConnect(this, channel, remoteIpAddress);
        }
    }

    public void fireIncomingMessage(Channel channel, String message) {
        for (TransportListener transportListener: this.listeners) {
            transportListener.incomingMessage(this, channel, message);
        }
    }

    public void fireDisconnect(Channel channel, int statusCode, Throwable throwable) {
        for (TransportListener transportListener: this.listeners) {
            transportListener.onDisconnect(this, channel, statusCode, throwable);
        }
    }

    public abstract void send(Channel channel, IPacket packet) throws IOException;

    public abstract void destroy() throws Exception;

    public abstract void close(Channel channel) throws IOException;

    public abstract TransportInfo getInfo();
}
