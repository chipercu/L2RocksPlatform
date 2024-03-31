package com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel;

import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.utils.ChannelIterator;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.service.remotecontroller.GrpcRemoteControllerRequest;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.RandomUtils;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.fuzzy.cluster.event.CauseNodeDisconnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelList {

    private final static Logger log = LoggerFactory.getLogger(ChannelList.class);

    public final com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channels channels;
    private final GrpcRemoteControllerRequest remoteControllerRequest;
    private final Map<UUID, List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel>> channelItems;

    public ChannelList(Channels channels, GrpcRemoteControllerRequest remoteControllerRequest) {
        this.channels = channels;
        this.remoteControllerRequest = remoteControllerRequest;
        this.channelItems = new HashMap<>();
    }

    public void addChannel(com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel channel) {
        Node remoteNode = channel.getRemoteNode().node;
        UUID remoteNodeRuntimeId = remoteNode.getRuntimeId();
        List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel> items = channelItems.get(remoteNodeRuntimeId);
        if (items == null) {
            synchronized (channelItems) {
                items = channelItems.get(remoteNodeRuntimeId);
                if (items == null) {
                    items = new CopyOnWriteArrayList<>();
                    channelItems.put(remoteNodeRuntimeId, items);
                }
            }
        }

        boolean fireEvent = false;
        synchronized (items) {
            items.add(channel);

            if (items.size() == 1) {
                fireEvent = true;
            }
        }

        log.debug("Add channel: {}, total: {}", channel, items.size());

        //Отправляем оповещение
        if (fireEvent) {
            channels.fireEventConnectNode(remoteNode);
        }
    }

    public void removeChannel(com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel channel, CauseNodeDisconnect cause) {
        ((com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl) channel).destroy();

        Node remoteNode = channel.getRemoteNode().node;

        UUID remoteNodeRuntimeId = remoteNode.getRuntimeId();
        List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel> items = channelItems.get(remoteNodeRuntimeId);
        if (items == null) return;

        boolean fireEvent = false;
        synchronized (items) {
            items.remove(channel);

            if (items.size() == 0) {
                fireEvent = true;
            }
        }

        log.debug("Remove channel: {}, total: {}", channel, items.size());

        //Кидаем ошибку по всем ожидающим запросам
        remoteControllerRequest.disconnectChannel(channel);

        //Отправляем оповещение
        if (fireEvent) {
            channels.fireEventDisconnectNode(remoteNode, cause);
        }
    }

    public com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel getRandomChannel(UUID nodeRutimeId, int attempt) {
        com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel channel = getRandomChannel(nodeRutimeId);
        if (channel != null) {
            return channel;
        } else if (attempt > 0) {
            log.debug("Attempt find channel to: {}, last: {}", nodeRutimeId, attempt);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                return null;
            }
            return getRandomChannel(nodeRutimeId, attempt - 1);
        } else {
            return null;
        }
    }

    public com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel getRandomChannel(UUID nodeRutimeId) {
        List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel> items = channelItems.get(nodeRutimeId);
        if (items == null) return null;
        synchronized (items) {
            int size = items.size();
            if (size == 0) return null;

            int index = new Random().nextInt(size);
            com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel result = null;
            int i = 0;
            for (com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel channel : items) {
                if (!channel.isAvailable()) continue;
                result = channel;
                if (i++ == index) break;
            }
            return result;
        }
    }

    public Set<UUID> getNodes() {
        HashSet<UUID> nodes = new HashSet<>();
        for (Map.Entry<UUID, List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel>> entry : channelItems.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                nodes.add(entry.getKey());
            }
        }
        return nodes;
    }

    public List<Node> getRemoteNodes() {
        List<Node> nodes = new ArrayList<>();
        for (List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel> items : channelItems.values()) {
            synchronized (items) {
                if (items.size() > 0) {
                    nodes.add(items.get(0).getRemoteNode().node);
                }
            }
        }
        return nodes;
    }

    public ChannelIterator getChannelIterator() {
        return new ChannelIterator(channelItems);
    }

    /**
     * Принудительно разрываем соединение с каналом
     * @param channel
     */
    public void killChannel(com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel channel, CauseNodeDisconnect cause) {
        com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl channelImpl = (com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl) channel;
        channelImpl.kill(cause.throwable);
        removeChannel(channel, cause);
    }

    public void sendBroadcast(PNetPackage netPackage) {
        for (List<com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel> iChannels : channelItems.values()) {
            for (Channel channel : iChannels) {
                if (!channel.isAvailable()) continue;

                com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl channelImpl = (ChannelImpl) channel;
                try {
                    channelImpl.send(netPackage);
                } catch (Exception e) {
                    log.error("Error send broadcast", e);
                }
            }
        }
    }
}
