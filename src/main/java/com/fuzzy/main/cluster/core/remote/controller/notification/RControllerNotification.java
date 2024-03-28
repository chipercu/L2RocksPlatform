package com.fuzzy.main.cluster.core.remote.controller.notification;

import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.main.cluster.core.remote.struct.RController;

/**
 * Created by kris on 02.11.16.
 */
public interface RControllerNotification extends RController {

	void notificationRegisterComponent(RuntimeComponentInfo componentInfo);

    void notificationUnRegisterComponent(int id);
}

