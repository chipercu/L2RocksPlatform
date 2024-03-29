package com.fuzzy.subsystem.core.tree.employee;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.function.Consumer;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.filterhandler.EmployeeFilterHandler;
import com.fuzzy.subsystem.core.graphql.query.tree.GTreeView;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystem.core.grouping.enumerator.EmployeeGroupingEnumerator;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeMonitoringGetter;
import com.fuzzy.subsystem.core.textfilter.DepartmentTextFilterGetter;
import com.fuzzy.subsystem.core.textfilter.EmployeeTextFilterGetter;
import com.fuzzy.subsystem.core.utils.EmployeeComparator;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.function.BiFunction;
import com.fuzzy.subsystems.graphql.enums.EnableMonitoringType;
import com.fuzzy.subsystems.graphql.input.GFilterOperation;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.graphql.input.GItemsFilter;
import com.fuzzy.subsystems.tree.Tree;
import com.fuzzy.subsystems.tree.TreeBuilder;
import com.fuzzy.subsystems.tree.TreeParam;
import com.fuzzy.subsystems.utils.CollectionUtils;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.util.*;

public class EmployeeTreeBuilder {

    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;
    private final RControllerEmployeeMonitoringGetter rControllerEmployeeMonitoringGetter;
    private final TreeBuilder<DepartmentReadable, EmployeeReadable> treeBuilder;
    private final EmployeeTreeChecker checker;
    private final EmployeeFilterHandler employeeFilterHandler;
    private final CoreConfigGetter coreConfigGetter;

    private EmployeeTreeBuilder(
            EmployeeTreeChecker checker,
            ResourceProvider resources
    ) {
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
        Set<RControllerEmployeeMonitoringGetter> rcontrollers = resources.getQueryRemoteControllers(RControllerEmployeeMonitoringGetter.class);
        if (rcontrollers.isEmpty()) {
            rControllerEmployeeMonitoringGetter = null;
        } else {
            rControllerEmployeeMonitoringGetter = rcontrollers.iterator().next();
        }
        this.treeBuilder = new TreeBuilder<>(
                resources.getReadableResource(DepartmentReadable.class),
                resources.getReadableResource(EmployeeReadable.class),
                new DepartmentGroupingEnumerator(resources),
                new EmployeeGroupingEnumerator(resources),
                new DepartmentTextFilterGetter(resources),
                new EmployeeTextFilterGetter(resources),
                checker
        );
        this.treeBuilder.setNodeComparator(
                (o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName())
        );
        this.employeeFilterHandler = new EmployeeFilterHandler(resources);
        this.coreConfigGetter = new CoreConfigGetter(resources);
        this.checker = checker;
    }

    public EmployeeTreeBuilder(ResourceProvider resources) {
        this(new EmployeeTreeChecker(resources), resources);
    }

    public Tree<DepartmentReadable, EmployeeReadable> build(EmployeeTreeParam param, ContextTransaction<?> contextTransaction)
            throws PlatformException {
        QueryTransaction transaction = contextTransaction.getTransaction();
        if (param.authEmployeeId != null) {
            checker.setAuthEmployeeId(param.authEmployeeId);
        }
        DisplayNameFormat displayNameFormat =
                coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, transaction);
        this.treeBuilder.setItemComparator(new EmployeeComparator(displayNameFormat));

        TreeParam treeParam = new TreeParam();
        treeParam.textFilter = param.textFilter;
        treeParam.paging = param.paging;
        treeParam.idFilter = doFiltering(param, contextTransaction);
        treeParam.alwaysComingData = param.alwaysComingData;
        if(Objects.nonNull(param.topElements)) {
            treeParam.topElements = param.topElements;
        }
        if (param.authEmployeeId != null && param.alwaysComingData != null &&
                (param.alwaysComingData.getNodes() != null && !param.alwaysComingData.getNodes().isEmpty() ||
                        param.alwaysComingData.getItems() != null && !param.alwaysComingData.getItems().isEmpty())) {
            ManagerEmployeeAccess access = checker.ensureAccess(transaction);
            HashSet<Long> departments = null;
            if (param.alwaysComingData.getNodes() != null) {
                departments = new HashSet<>();
                for (Long departmentId : param.alwaysComingData.getNodes()) {
                    if (access.checkDepartment(departmentId)) {
                        departments.add(departmentId);
                    }
                }
            }
            HashSet<Long> employees = null;
            if (param.alwaysComingData.getItems() != null) {
                employees = new HashSet<>();
                for (Long employeeId : param.alwaysComingData.getItems()) {
                    if (access.checkEmployee(employeeId)) {
                        employees.add(employeeId);
                    }
                }
            }
            treeParam.alwaysComingData = new GInputNodesItems(departments, employees);
        }
        treeParam.view = GTreeView.NODE_ITEM;
        return this.treeBuilder.build(treeParam, transaction);
    }

    private GInputNodesItems doFiltering(EmployeeTreeParam param, ContextTransaction<?> contextTransaction) throws PlatformException {
        QueryTransaction transaction = contextTransaction.getTransaction();
        if (param.accessRoleFilter != null ||
                param.authenticationFilter != null ||
                (param.monitoringTypes != null && !param.monitoringTypes.isEmpty()) ||
                param.idFilter != null) {
            HashSet<Long> employees = employeeFilterHandler.get(param.idFilter, transaction);
            employees = filterByItemsFilter(employees, param.accessRoleFilter, this::getEmployeesByAccessRoles, transaction);
            employees = filterByItemsFilter(employees, param.authenticationFilter, this::getEmployeesByAuthentications, transaction);
            employees = filterByEnabledMonitoring(employees, param, contextTransaction);

            return new GInputNodesItems(null, employees);
        }
        return null;
    }

    private HashSet<Long> filterByItemsFilter(HashSet<Long> employees,
                                              GItemsFilter filter,
                                              BiFunction<Collection<Long>, QueryTransaction, HashSet<Long>> employeeGetter,
                                              QueryTransaction transaction) throws PlatformException {
        if (filter == null) {
            return employees;
        }
        GFilterOperation filterOperation = filter.getOperation();
        HashSet<Long> result;
        if (filterOperation == GFilterOperation.INCLUDE || filterOperation == GFilterOperation.EXCLUDE) {
            HashSet<Long> employeesWithGivenEntities = employeeGetter.apply(filter.getItems(), transaction);
            if (filterOperation == GFilterOperation.INCLUDE) {
                result = CollectionUtils.retainCollections(employees, employeesWithGivenEntities);
            } else {
                employees.removeAll(employeesWithGivenEntities);
                result = employees;
            }
        } else if (filterOperation == GFilterOperation.EMPTY || filterOperation == GFilterOperation.NONEMPTY) {
            HashSet<Long> employeesWithSomeEntity = employeeGetter.apply(null, transaction);
            if (filterOperation == GFilterOperation.NONEMPTY) {
                result = CollectionUtils.retainCollections(employees, employeesWithSomeEntity);
            } else {
                employees.removeAll(employeesWithSomeEntity);
                result = employees;
            }
        } else {
            throw GeneralExceptionBuilder.buildUnexpectedBehaviourException("unknown filter operation");
        }
        return result;
    }

    private HashSet<Long> getEmployeesByAccessRoles(Collection<Long> accessRoles,
                                                    QueryTransaction transaction) throws PlatformException {
        HashSet<Long> employees = new HashSet<>();
        Consumer<EmployeeAccessRoleReadable> action =
                employeeAccessRole -> employees.add(employeeAccessRole.getEmployeeId());
        if (accessRoles != null) {
            for (Long accessRoleId : accessRoles) {
                HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
                employeeAccessRoleReadableResource.forEach(filter, action, transaction);
            }
        } else {
            employeeAccessRoleReadableResource.forEach(action, transaction);
        }
        return employees;
    }

    private HashSet<Long> getEmployeesByAuthentications(Collection<Long> authentications,
                                                        QueryTransaction transaction) throws PlatformException {
        HashSet<Long> employees = new HashSet<>();
        Consumer<EmployeeAuthenticationReadable> action =
                employeeAuthentication -> employees.add(employeeAuthentication.getEmployeeId());
        if (authentications != null) {
            for (Long authenticationId : authentications) {
                HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, authenticationId);
                employeeAuthenticationReadableResource.forEach(filter, action, transaction);
            }
        } else {
            employeeAuthenticationReadableResource.forEach(action, transaction);
        }
        return employees;
    }

    private HashSet<Long> filterByEnabledMonitoring(HashSet<Long> employees, EmployeeTreeParam param, ContextTransaction<?> transaction) throws PlatformException {
        ArrayList<EnableMonitoringType> monitoringTypes = param.monitoringTypes;
        if (monitoringTypes != null && !monitoringTypes.isEmpty()) {
            if (rControllerEmployeeMonitoringGetter == null) {
                throw CoreExceptionBuilder.buildNotActivePlatformException("com.infomaximum.subsystem.monitoring");
            }
            return rControllerEmployeeMonitoringGetter.getEmployeesWithEnabledMonitoring(monitoringTypes, employees, transaction);
        }
        return employees;
    }
}