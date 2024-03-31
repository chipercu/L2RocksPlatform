package com.fuzzy.cluster.event;

import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.event.CauseNodeDisconnect;

public interface UpdateNodeConnect {

    void onConnect(Node node);

    void onDisconnect(Node node, CauseNodeDisconnect cause);

}
