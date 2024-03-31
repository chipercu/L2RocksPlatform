package com.fuzzy.subsystem.core.emailmessages;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import org.apache.commons.lang3.StringUtils;

public class EmployeeDisplayNameForMessageBuilder {

    private final CoreConfigGetter coreConfigGetter;
    private DisplayNameFormat displayNameFormat;


    public EmployeeDisplayNameForMessageBuilder(ResourceProvider resources) {
        coreConfigGetter = new CoreConfigGetter(resources);
        displayNameFormat = null;
    }

    public String build(EmployeeReadable employee, QueryTransaction transaction) throws PlatformException {
        String displayName;
        if (!StringUtils.isEmpty(employee.getFirstName())
                || !StringUtils.isEmpty(employee.getSecondName())
                || !StringUtils.isEmpty(employee.getPatronymic())) {
            displayName = employee.getDisplayName(getDisplayNameFormat(transaction));
        } else if (!StringUtils.isEmpty(employee.getLogin())) {
            displayName = employee.getLogin();
        } else if (!StringUtils.isEmpty(employee.getEmail())) {
            displayName = employee.getEmail();
        } else {
            displayName = "";
        }
        return displayName;
    }

    private DisplayNameFormat getDisplayNameFormat(QueryTransaction transaction) throws PlatformException {
        if (displayNameFormat == null) {
            displayNameFormat =
                    coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, transaction);
        }
        return displayNameFormat;
    }
}
