package com.fuzzy.main.cluster.component.service.remote;

import com.fuzzy.main.cluster.core.remote.struct.RController;

/**
 * Created by kris on 28.10.16.
 */
public interface RControllerInputStream extends RController {

	byte[] next(int id, int limit) throws Exception;

}
