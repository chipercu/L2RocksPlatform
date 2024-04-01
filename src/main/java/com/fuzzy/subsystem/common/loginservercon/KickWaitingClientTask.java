package com.fuzzy.subsystem.common.loginservercon;

import com.fuzzy.subsystem.extensions.network.L2GameClient;

/**
 * @Author: Death
 * @Date: 13/11/2007
 * @Time: 20:14:14
 */
public class KickWaitingClientTask extends com.fuzzy.subsystem.common.RunnableImpl {
    private final L2GameClient client;

    public KickWaitingClientTask(L2GameClient client) {
        this.client = client;
    }

    public void runImpl() {
        client.closeNow(false);
    }
}
