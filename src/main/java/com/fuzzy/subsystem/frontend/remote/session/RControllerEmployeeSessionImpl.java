package com.fuzzy.subsystem.frontend.remote.session;

import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RControllerEmployeeSessionImpl extends AbstractQueryRController<FrontendSubsystem> implements RControllerEmployeeSession {

    private SessionServiceEmployee sessionService;

    public RControllerEmployeeSessionImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.sessionService = component.getSessionService();
    }

    @Override
    public ArrayList<SessionEmployeeRemoteAdapter> getSessions(long employeeId) {
        return sessionService.getSessions().stream()
                .filter(s -> s.employeeId == employeeId)
                .map(SessionEmployeeRemoteAdapter::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
