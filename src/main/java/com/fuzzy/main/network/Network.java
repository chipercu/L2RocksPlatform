package com.fuzzy.main.network;

import com.fuzzy.main.network.event.NetworkListener;
import com.fuzzy.main.network.struct.info.NetworkInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Vladimir Ulitin
 * Date: 01.02.12
 * Time: 21:00
 */
public interface Network extends AutoCloseable {

    void addNetworkListener(NetworkListener listener);

    void removeNetworkListener(NetworkListener listener);

    NetworkInfo getInfo();

    @Override
    void close();
}
