package com.fuzzy.subsystem.core.remote.logon;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.subsystem.core.config.LogonType;

public interface RControllerEmployeeLogon extends QueryRemoteController {

    EmployeeLogin logon(String login, String passwordHash, ContextTransactionRequest context)
            throws PlatformException;

    LogonType getLogonType() throws PlatformException;
}