package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel;

import com.fuzzy.main.cluster.Node;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.service.remotecontroller.GrpcRemoteControllerRequest;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelList {

    private final static Logger log = LoggerFactory.getLogger(ChannelList.class);

    public final Channels channels;
    private final GrpcRemoteControllerRequest remoteControllerRequest;
    private final Map<UUID, List<Channel>> channelItems;

    public ChannelList(Channels channels, GrpcRemoteControllerRequest remoteControllerRequest) {
        this.channels = channels;
        this.remoteControllerRequest = remoteControllerRequest;
        this.channelItems = new HashMap<>();
    }

    public void addChannel(Channel channel) {
        Node remoteNode = channel.getRemoteNode().node;
        UUID remoteNodeRuntimeId = remoteNode.getRuntimeId();
        List<Channel> items = channelItems.get(remoteNodeRuntimeId);
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

    public void removeChannel(Channel channel) {
        ((ChannelImpl) channel).destroy();

        Node remoteNode = channel.getRemoteNode().node;

        UUID remoteNodeRuntimeId = remoteNode.getRuntimeId();
        List<Channel> items = channelItems.get(remoteNodeRuntimeId);
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
            channels.fireEventDisconnectNode(remoteNode);
        }
    }

    public Channel getRandomChannel(UUID nodeRutimeId, int attempt) {
        Channel channel = getRandomChannel(nodeRutimeId);
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

    public Channel getRandomChannel(UUID nodeRutimeId) {
        List<Channel> items = channelItems.get(nodeRutimeId);
        if (items == null) return null;
        synchronized (items) {
            int size = items.size();
            if (size == 0) return null;

            int index = new Random().nextInt(size);
            Channel result = null;
            int i = 0;
            for (Channel channel : items) {
                if (!channel.isAvailable()) continue;
                result = channel;
                if (i++ == index) break;
            }
            return result;
        }
    }

    public Set<UUID> getNodes() {
        HashSet<UUID> nodes = new HashSet<>();
        for (Map.Entry<UUID, List<Channel>> entry : channelItems.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                nodes.add(entry.getKey());
            }
        }
        return nodes;
    }

    public List<Node> getRemoteNodes() {
        List<Node> nodes = new ArrayList<>();
        for (List<Channel> items : channelItems.values()) {
            synchronized (items) {
                if (items.size() > 0) {
                    nodes.add(items.get(0).getRemoteNode().node);
                }
            }
        }
        return nodes;
    }

    public void sendBroadcast(PNetPackage netPackage) {
        for (List<Channel> iChannels : channelItems.values()) {
            for (Channel channel : iChannels) {
                if (!channel.isAvailable()) continue;

                ChannelImpl channelImpl = (ChannelImpl) channel;
                try {
                    channelImpl.sent(netPackage);
                } catch (Exception e) {
                    log.error("Error send broadcast", e);
                }
            }
        }
    }
}
