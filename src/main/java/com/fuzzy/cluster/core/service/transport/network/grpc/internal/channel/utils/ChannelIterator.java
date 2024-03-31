package com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.utils;

import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChannelIterator implements Iterator<Channel> {

    private final Iterator<Map.Entry<UUID, List<Channel>>> channelItemsIterator;
    private Iterator<Channel> currentListIterator;

    private Channel nextChannel;

    public ChannelIterator(Map<UUID, List<Channel>> channelItems) {
        this.channelItemsIterator = channelItems.entrySet().iterator();
        if (channelItemsIterator.hasNext()) {
            currentListIterator = channelItemsIterator.next().getValue().iterator();
        }
        next();
    }

    @Override
    public boolean hasNext() {
        return (nextChannel != null);
    }

    @Override
    public Channel next() {
        Channel currentChannel = nextChannel;
        nextChannel = getNextChannel();
        return currentChannel;
    }

    private Channel getNextChannel() {
        while (true) {
            if (currentListIterator == null) {
                return null;
            }

            if (currentListIterator.hasNext()) {
                Channel channel = currentListIterator.next();
                if (channel != null && channel.isAvailable()) {
                    return channel;
                }
            } else if (channelItemsIterator.hasNext()) {
                currentListIterator = channelItemsIterator.next().getValue().iterator();
            } else {
                currentListIterator = null;
            }
        }
    }
}
