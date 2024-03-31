package com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.service;

import com.fuzzy.cluster.core.service.transport.network.grpc.exception.ClusterGrpcPingPongTimeoutException;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelList;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.utils.ChannelIterator;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackagePing;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackagePong;
import com.fuzzy.cluster.event.CauseNodeDisconnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PingPongService {

    private final static Logger log = LoggerFactory.getLogger(PingPongService.class);

    private final ChannelList channelList;
    private final ScheduledExecutorService scheduledServicePingPongExecute;

    private final Duration timeout;

    private Set<Channel> waitPong;

    public PingPongService(ChannelList channelList, Duration interval, Duration timeout) {
        this.channelList = channelList;
        this.timeout = timeout;
        this.waitPong = ConcurrentHashMap.newKeySet();

        this.scheduledServicePingPongExecute = Executors.newSingleThreadScheduledExecutor();
        this.scheduledServicePingPongExecute.scheduleWithFixedDelay(() -> execute(), 1, interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void execute() {
        try {
            if (!waitPong.isEmpty()) {
                throw new IllegalStateException();
            }

            PNetPackage pNetPackagePing = PNetPackage.newBuilder().setPing(
                    PNetPackagePing.newBuilder().setTime(System.currentTimeMillis()).build()
            ).build();

            ChannelIterator iterator = channelList.getChannelIterator();
            while (iterator.hasNext()) {
                ChannelImpl channel = (ChannelImpl) iterator.next();
                try {
                    channel.send(pNetPackagePing);
                    waitPong.add(channel);
                } catch (Exception e) {
                    killChannelWithException(channel, e);
                    waitPong.remove(channel);
                }
            }

            Thread.sleep(timeout.toMillis());

            Iterator<Channel> iWaitPong = waitPong.iterator();
            while (iWaitPong.hasNext()) {
                Channel channel = iWaitPong.next();
                killChannelWithTimeout(channel);
                iWaitPong.remove();
            }
        } catch (Throwable e) {
            channelList.channels.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    private void killChannelWithTimeout(Channel channel) {
        log.debug("Kill channel with timeout: {}", channel);
        //Отправляем запрос на прибитие
        channelList.killChannel(channel, new CauseNodeDisconnect(CauseNodeDisconnect.Type.TIMEOUT, new ClusterGrpcPingPongTimeoutException()));
    }

    private void killChannelWithException(Channel channel, Throwable e) {
        log.debug("Kill channel with exception: {}", channel, e);
        //Отправляем запрос на прибитие
        channelList.killChannel(channel, new CauseNodeDisconnect(CauseNodeDisconnect.Type.EXCEPTION, e));
    }

    public void handleIncomingPong(ChannelImpl channel, PNetPackagePong ping) {
        waitPong.remove(channel);
    }

    public void handleIncomingPing(ChannelImpl channel, PNetPackagePing ping) {
        PNetPackage pNetPackagePong = PNetPackage.newBuilder().setPong(
                PNetPackagePong.newBuilder().setTime(ping.getTime()).build()
        ).build();
        try {
            channel.send(pNetPackagePong);
        } catch (Exception e) {
            log.error("Exception send 'pong' package, channel: {}", channel, e);
        }
    }


    public void close() {
        scheduledServicePingPongExecute.shutdown();
    }
}
