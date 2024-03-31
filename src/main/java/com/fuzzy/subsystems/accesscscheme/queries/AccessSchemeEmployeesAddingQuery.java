package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.queries.service.AccessSchemeElementAddingService;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

public class AccessSchemeEmployeesAddingQuery<
        O, T,
        U extends AccessSchemeItem<Long, O, T>> extends GraphQLQuery<RemoteObject, Boolean> {

    private final AccessSchemeProcessor<Long, O, T, U> accessSchemeProcessor;
    private final SubjectObjectChecker<Long, O> checker;
    private final O objectId;
    private final HashSet<Long> departmentsIds;
    private final HashSet<Long> employeesIds;
    private final T operation;
    private DepartmentGrouping departmentGrouping;
    private AccessSchemeElementAddingService<Long, O, T, U> accessSchemeElementAddingService;

    public AccessSchemeEmployeesAddingQuery(@NonNull AccessSchemeProcessor<Long, O, T, U> accessSchemeProcessor,
                                            @NonNull SubjectObjectChecker<Long, O> checker,
                                            @NonNull O objectId,
                                            @NonNull HashSet<Long> departmentsIds,
                                            @NonNull HashSet<Long> employeesIds,
                                            @NonNull T operation) {
        this.accessSchemeProcessor = accessSchemeProcessor;
        this.checker = checker;
        this.objectId = objectId;
        this.departmentsIds = departmentsIds;
        this.employeesIds = employeesIds;
        this.operation = operation;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        this.departmentGrouping = new DepartmentGrouping(resources);
        accessSchemeElementAddingService = new AccessSchemeElementAddingService<>(resources, accessSchemeProcessor, checker);
    }

    @Override
    public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!departmentsIds.isEmpty()) {
            for (Long departmentId : departmentsIds) {
                if (departmentId == null) {
                    continue;
                }
                employeesIds.addAll(departmentGrouping.getChildItemsRecursively(departmentId, transaction));
            }
        }
        return accessSchemeElementAddingService.addSubjects(this.objectId, this.employeesIds, this.operation, context);
    }
}
