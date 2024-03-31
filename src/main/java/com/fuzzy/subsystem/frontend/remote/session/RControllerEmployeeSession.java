package com.fuzzy.subsystem.frontend.remote.session;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

import java.util.ArrayList;

public interface RControllerEmployeeSession extends QueryRemoteController {

    ArrayList<SessionEmployeeRemoteAdapter> getSessions(long employeeId) throws PlatformException;
}
