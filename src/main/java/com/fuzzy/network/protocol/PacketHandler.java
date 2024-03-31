package com.fuzzy.network.protocol;

import com.fuzzy.network.exception.NetworkException;
import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.session.Session;

import java.util.concurrent.CompletableFuture;

/**
 * Created by kris on 26.08.16.
 */
public interface PacketHandler {

    CompletableFuture<IPacket[]> exec(Session session, IPacket packet);

    abstract class Builder {

        public abstract PacketHandler build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException;

    }
}
