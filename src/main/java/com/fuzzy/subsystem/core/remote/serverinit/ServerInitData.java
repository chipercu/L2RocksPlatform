package com.fuzzy.subsystem.core.remote.serverinit;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystem.core.config.Language;

public class ServerInitData implements RemoteObject {

    private long employeeId;
    private Language serverLanguage;
    private long administratorAccessRoleId;
    private long securityAdministratorAccessRoleId;

    public ServerInitData(
            long employeeId,
            Language serverLanguage,
            long administratorAccessRoleId,
            long securityAdministratorAccessRoleId
    ) {
        this.employeeId = employeeId;
        this.serverLanguage = serverLanguage;
        this.administratorAccessRoleId = administratorAccessRoleId;
        this.securityAdministratorAccessRoleId = securityAdministratorAccessRoleId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public Language getServerLanguage() {
        return serverLanguage;
    }

    public long getAdministratorAccessRoleId() {
        return administratorAccessRoleId;
    }

    public long getSecurityAdministratorAccessRoleId() {
        return securityAdministratorAccessRoleId;
    }
}
