package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.textfilter.EmployeeTextFilterGetter;
import com.fuzzy.subsystems.accesscscheme.AccessSchemeElementListBuilder;
import com.fuzzy.subsystems.accesscscheme.GAccessSchemeOperation;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.entityelements.Elements;
import com.fuzzy.subsystems.remote.Identifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AccessSchemeEmployeeListBuilder<
        K extends Comparable<K>,
        T extends Identifiable<K> & AccessSchemeItem<Long, ?, ? extends GAccessSchemeOperation>> extends AccessSchemeElementListBuilder<K, T> {

    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private ManagerEmployeeAccess access;

    public AccessSchemeEmployeeListBuilder(@NonNull ResourceProvider resources) {
        super(new EmployeeTextFilterGetter(resources));
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    @Override
    protected @NonNull Long getElementId(@NonNull T item) {
        return item.getSubjectId();
    }

    @Override
    protected boolean checkElementAccess(@NonNull Long employeeId,
                                         @NonNull ContextTransaction<?> context) throws PlatformException {
        if (access == null) {
            if (context instanceof ContextTransactionRequest) {
                access = managerEmployeeAccessGetter.getAccess((ContextTransactionRequest) context);
            } else {
                access = new ManagerEmployeeAccess(new Elements());
            }
        }
        return access.checkEmployee(employeeId);
    }
}
