package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.google.common.collect.Lists;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.database.domainobject.filter.IdFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeephone.EmployeePhoneReadable;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessReadable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessReadable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessReadable;
import com.fuzzy.subsystem.core.entityprovider.entity.EmployeeEntity;
import com.fuzzy.subsystem.core.remote.additionalfieldvaluegetter.RCAdditionalFieldValueGetter;
import com.fuzzy.subsystem.core.remote.employee.EmployeeAccountMetaData;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeAccountControl;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeMonitoringGetter;
import com.fuzzy.subsystem.core.remote.employeeauthenticationchecker.RCEmployeeAuthenticationChecker;
import com.fuzzy.subsystem.core.remote.fieldsgetter.AdditionalFieldDescription;
import com.fuzzy.subsystem.core.remote.fieldsgetter.RCFieldsGetter;
import com.fuzzy.subsystem.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;
import com.fuzzy.subsystems.remote.Optional;
import com.fuzzy.subsystems.remote.RCExecutor;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmployeeDataSource implements DataSourceProvider<EmployeeEntity> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDataSource.class);
    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private ReadableResource<DepartmentReadable> departmentReadableResource;
    private RCExecutor<RControllerEmployeeMonitoringGetter> employeeMonitoringGetter;
    private RCExecutor<RControllerEmployeeAccountControl> employeeAccountGetter;
    private RCEmployeeAuthenticationChecker authenticationChecker;
    private RCFieldsGetter rcFieldsGetter;
    private RCAdditionalFieldValueGetter rcAdditionalFieldValueGetter;
    private ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private ReadableResource<EmployeePhoneReadable> employeePhoneReadableResource;
    private CoreConfigGetter coreConfigGetter;
    private Set<Long> allEmployeeIds = null;
    private Set<Long> allDepartmentsIds = null;
    private final Map<Long, DepartmentInfo> departmentsCache = new HashMap<>();
    private List<AdditionalFieldDescription> additionalFields = new ArrayList<>();
    private EmployeeDataSourceFieldHandler fieldHandler;
    private DisplayNameFormat displayNameFormat;
    private ReadableResource<ManagerAllAccessReadable> allAccessReadableResource;
    private ReadableResource<ManagerEmployeeAccessReadable> managerEmployeeAccessReadableResource;
    private ReadableResource<ManagerDepartmentAccessReadable> managerDepartmentAccessReadableResource;
    private EmployeeDepartmentTree employeeDepartmentTree;


    @Override
    public void prepare(ResourceProvider resources) {
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        employeeMonitoringGetter = new RCExecutor<>(resources, RControllerEmployeeMonitoringGetter.class);
        employeeAccountGetter = new RCExecutor<>(resources, RControllerEmployeeAccountControl.class);
        authenticationChecker = resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
        rcFieldsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCFieldsGetter.class);
        rcAdditionalFieldValueGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalFieldValueGetter.class);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        employeePhoneReadableResource = resources.getReadableResource(EmployeePhoneReadable.class);
        coreConfigGetter = new CoreConfigGetter(resources);
        allAccessReadableResource = resources.getReadableResource(ManagerAllAccessReadable.class);
        managerEmployeeAccessReadableResource = resources.getReadableResource(ManagerEmployeeAccessReadable.class);
        managerDepartmentAccessReadableResource = resources.getReadableResource(ManagerDepartmentAccessReadable.class);
    }

    @Override
    public void setExtractFields(List<EntityFieldInfo> fields) {
        fieldHandler = new EmployeeDataSourceFieldHandler(fields);
    }

    @Override
    public DataSourceIterator<EmployeeEntity> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException {
        long getAllEmployeeIdsTime = 0;
        long getAllDepartmentIdsTime = 0;
        long getAdditionalFieldListTime = 0;

        long baseFieldTime = 0;
        long phoneFieldTime = 0;
        long monitoringFieldTime = 0;
        long accessRolesFieldTime = 0;
        long managerEmployeeDepartmentTime = 0;
        long departmentListTime = 0;
        long additionalFieldTime = 0;

        ContextTransactionImpl contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);

        if (fieldHandler.isAccessToEmployeeIds()) {
            if (Objects.isNull(allEmployeeIds)) {
                long getAllEmployeeIdsStart = System.currentTimeMillis();
                allEmployeeIds = getAllEmployeeIds(transaction);
                getAllEmployeeIdsTime = System.currentTimeMillis() - getAllEmployeeIdsStart;
            }
        }

        if (fieldHandler.isAccessToDepartmentIds()) {
            if (Objects.isNull(allDepartmentsIds)) {
                long getAllDepartmentIdsStart = System.currentTimeMillis();
                allDepartmentsIds = getAllDepartmentIds(transaction);
                getAllDepartmentIdsTime = System.currentTimeMillis() - getAllDepartmentIdsStart;
            }
        }


        if (fieldHandler.isAdditionalFieldsNames() || fieldHandler.isAdditionalFieldsValues()) {
            if (additionalFields.isEmpty()) {
                long getAdditionalFieldListStart = System.currentTimeMillis();
                this.additionalFields.addAll(rcFieldsGetter.getAdditionalFields(EmployeeReadable.class.getName(), contextTransaction));
                this.additionalFields.sort(Comparator.comparing(AdditionalFieldDescription::getAdditionalFieldId));
                getAdditionalFieldListTime = System.currentTimeMillis() - getAdditionalFieldListStart;
            }
        }


        if (fieldHandler.isName() || fieldHandler.isAdditionalFieldsValues()) {
            if (Objects.isNull(displayNameFormat)) {
                displayNameFormat =
                        coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, contextTransaction.getTransaction());
            }
        }


        List<EmployeeEntity> containers = Lists.newArrayList();
        try (IteratorEntity<EmployeeReadable> employee = employeeReadableResource.findAll(new IdFilter(lastProcessedId + 1, Long.MAX_VALUE), transaction)) {
            while (employee.hasNext() && containers.size() < limit) {


                EmployeeEntity.Builder builder = EmployeeEntity.newBuilder();
                EmployeeReadable employeeReadable = employee.next();

                long baseFieldStart = System.currentTimeMillis();
                long employeeReadableId = employeeReadable.getId();
                setBaseFields(builder, employeeReadable);
                setAuthenticationAssigned(builder, employeeReadableId, transaction);
                baseFieldTime += System.currentTimeMillis() - baseFieldStart;


                final long singlePhonesTime = setPhones(builder, employeeReadableId, transaction);
                phoneFieldTime += singlePhonesTime;

                final long singleMonitoringFieldsTime = setMonitoringFields(builder, employeeReadableId, contextTransaction);
                monitoringFieldTime += singleMonitoringFieldsTime;


                final long singleAccessRolesFieldTime = setAccessRoles(builder, employeeReadableId, transaction);
                accessRolesFieldTime += singleAccessRolesFieldTime;


                final long singleEmployeeDepartmentTime = setManagerEmployeeDepartments(builder, employeeReadableId, transaction);
                managerEmployeeDepartmentTime += singleEmployeeDepartmentTime;


                Long departmentId = employeeReadable.getDepartmentId();


                final long singleDepartmentListTime = setDepartmentList(builder, departmentId, transaction);
                departmentListTime += singleDepartmentListTime;


                final long singleAdditionalFieldsTime = setAdditionalFields(builder, employeeReadableId, contextTransaction);

                additionalFieldTime += singleAdditionalFieldsTime;
                containers.add(builder.build());
            }
        }

        log.info("EmployeeDataSource execute operation " +
                        "AllEmployeeIdsTime:{}, " +
                        "AllDepartmentIdsTime:{}," +
                        " AdditionalFieldListTime:{}," +
                        " baseFieldTime:{}," +
                        " phoneFieldTime:{}," +
                        " monitoringFieldTime:{}," +
                        " accessRolesFieldTime:{}," +
                        " managerEmployeeDepartmentTime:{}," +
                        " departmentListTime:{}," +
                        " additionalFieldTime:{}"
                , getAllEmployeeIdsTime
                , getAllDepartmentIdsTime
                , getAdditionalFieldListTime
                , baseFieldTime
                , phoneFieldTime
                , monitoringFieldTime
                , accessRolesFieldTime
                , managerEmployeeDepartmentTime
                , departmentListTime
                , additionalFieldTime
        );


        return new BaseSourceIterator<>(containers);
    }

    private long setAdditionalFields(EmployeeEntity.Builder builder, long employeeReadableId, ContextTransactionImpl contextTransaction) throws PlatformException {
        long additionalFieldStart = System.currentTimeMillis();

        if (fieldHandler.isAdditionalFieldsNames() || fieldHandler.isAdditionalFieldsValues()) {
            for (AdditionalFieldDescription additionalField : additionalFields) {
                if (fieldHandler.isAdditionalFieldsNames()) {
                    builder.addAdditionalFieldsName(additionalField.getName());
                }
                if (fieldHandler.isAdditionalFieldsValues()) {
                    builder.addAdditionalFieldsValue(getFieldValue(additionalField.getAdditionalFieldId(), employeeReadableId, contextTransaction));
                }
            }
        }
        return System.currentTimeMillis() - additionalFieldStart;
    }

    private long setDepartmentList(EmployeeEntity.Builder builder, Long departmentId, QueryTransaction transaction) throws PlatformException {
        long departmentListStart = System.currentTimeMillis();
        if (fieldHandler.isDepartmentIds() || fieldHandler.isDepartments()) {
            while (Objects.nonNull(departmentId) && departmentId > 0L) {

                final DepartmentInfo departmentInfo = departmentsCache.get(departmentId);
                if (Objects.nonNull(departmentInfo)) {
                    if (fieldHandler.isDepartments()) {
                        builder.addDepartment(departmentInfo.getName());
                    }
                    if (fieldHandler.isDepartmentIds()) {
                        builder.addDepartmentId(departmentInfo.getId());
                    }
                    departmentId = departmentInfo.getParentId();
                } else {
                    DepartmentReadable departmentReadable = departmentReadableResource.get(departmentId, transaction);
                    final String departmentName = departmentReadable.getName();
                    if (fieldHandler.isDepartments()) {
                        builder.addDepartment(departmentName);
                    }
                    if (fieldHandler.isDepartmentIds()) {
                        builder.addDepartmentId(departmentId);
                    }
                    final Long parentDepartmentId = departmentReadable.getParentDepartmentId();
                    departmentsCache.put(departmentId, new DepartmentInfo(departmentId, departmentName, parentDepartmentId));
                    departmentId = parentDepartmentId;
                }
            }
        }
        return System.currentTimeMillis() - departmentListStart;
    }

    HashMap<Long, Set<Long>> managerDepartmentAccessCache;
    HashMap<Long, Set<Long>> managerEmployeeAccessCache;


    private HashMap<Long, Set<Long>> initManagerDepartmentAccessCache(QueryTransaction transaction) throws PlatformException {
        final HashMap<Long, Set<Long>> managerDepartmentAccessMap = new HashMap<>();
        try (IteratorEntity<ManagerDepartmentAccessReadable> iterator = managerDepartmentAccessReadableResource.iterator(transaction)) {
            while (iterator.hasNext()) {
                final ManagerDepartmentAccessReadable departmentAccessReadable = iterator.next();
                final Long managerId = departmentAccessReadable.getManagerId();
                final Long departmentId = departmentAccessReadable.getDepartmentId();


                Set<Long> depIds = managerDepartmentAccessMap.get(managerId);
                if (Objects.isNull(depIds)) {
                    depIds = new HashSet<>();
                    depIds.add(departmentId);
                    managerDepartmentAccessMap.put(managerId, depIds);
                } else {
                    depIds.add(departmentId);
                }
            }
        }
        return managerDepartmentAccessMap;
    }

    private HashMap<Long, Set<Long>> initManagerEmployeeAccessCache(QueryTransaction transaction) throws PlatformException {
        HashMap<Long, Set<Long>> managerEmployeeAccessMap = new HashMap<>();
        try (IteratorEntity<ManagerEmployeeAccessReadable> iterator = managerEmployeeAccessReadableResource.iterator(transaction)) {
            while (iterator.hasNext()) {
                final ManagerEmployeeAccessReadable employeeAccessReadable = iterator.next();
                final Long managerId = employeeAccessReadable.getManagerId();
                final Long employeeId = employeeAccessReadable.getEmployeeId();
                Set<Long> empIds = managerEmployeeAccessMap.get(managerId);
                if (Objects.isNull(empIds)) {
                    empIds = new HashSet<>();
                    empIds.add(employeeId);
                    managerEmployeeAccessMap.put(managerId, empIds);
                } else {
                    empIds.add(employeeId);
                }
            }
        }
        return managerEmployeeAccessMap;
    }

    private long setManagerEmployeeDepartments(EmployeeEntity.Builder builder, long employeeReadableId, QueryTransaction transaction) throws PlatformException {
        long managerEmployeeDepartmentStart = System.currentTimeMillis();

        if (fieldHandler.isAccessToEmployeeIds() || fieldHandler.isAccessToDepartmentIds() || fieldHandler.isAllEmployeeAccess()) {
            boolean allEmployeeAccess = false;
            if (fieldHandler.isAllEmployeeAccess()) {
                final ManagerAllAccessReadable allAccessReadable
                        = allAccessReadableResource.find(new HashFilter(ManagerAllAccessReadable.FIELD_MANAGER_ID, employeeReadableId), transaction);
                allEmployeeAccess = allAccessReadable != null;
                builder.setAllEmployeeAccess(allEmployeeAccess);
            }
            if (fieldHandler.isAccessToEmployeeIds() || fieldHandler.isAccessToDepartmentIds()) {

                if (Objects.isNull(managerDepartmentAccessCache)) {
                    managerDepartmentAccessCache = initManagerDepartmentAccessCache(transaction);
                }

                if (Objects.isNull(managerEmployeeAccessCache)) {
                    managerEmployeeAccessCache = initManagerEmployeeAccessCache(transaction);
                }


                if (Objects.isNull(employeeDepartmentTree)) {
                    employeeDepartmentTree = feelTree(transaction);
                }
                if (allEmployeeAccess) {
                    if (fieldHandler.isAccessToEmployeeIds()) {
                        allEmployeeIds.forEach(builder::addAccessToEmployeeIds);
                    }
                    if (fieldHandler.isAccessToDepartmentIds()) {
                        allDepartmentsIds.forEach(builder::addAccessToDepartmentIds);
                    }
                } else {
                    Set<Long> tmpDepartmentAccessIds = new HashSet<>();
                    Set<Long> tmpEmployeesAccessIds = new HashSet<>();

                    final Set<Long> depIds = managerDepartmentAccessCache.get(employeeReadableId);
                    if (Objects.nonNull(depIds)) {
                        for (Long depId : depIds) {
                            initDepartmentEmployeesIds(employeeDepartmentTree, depId, tmpDepartmentAccessIds, tmpEmployeesAccessIds);
                        }
                    }
                    if (fieldHandler.isAccessToDepartmentIds()) {
                        tmpDepartmentAccessIds.forEach(builder::addAccessToDepartmentIds);
                    }
                    if (fieldHandler.isAccessToEmployeeIds()) {
                        Set<Long> managerEmployeeAccessDirect = new HashSet<>();
                        final Set<Long> empIds = managerEmployeeAccessCache.get(employeeReadableId);
                        if (Objects.nonNull(empIds)) {
                            managerEmployeeAccessDirect.addAll(empIds);
                        }
                        managerEmployeeAccessDirect.addAll(tmpEmployeesAccessIds);
                        managerEmployeeAccessDirect.forEach(builder::addAccessToEmployeeIds);
                    }
                }
            }
        }

        return System.currentTimeMillis() - managerEmployeeDepartmentStart;
    }


    private void initDepartmentEmployeesIds(EmployeeDepartmentTree tree, Long departmentId, Set<Long> tmpDepartmentAccessIds, Set<Long> tmpEmployeeAccessIds) {
        final DepartmentNode departmentNode = tree.getDepartmentElementsCache().get(departmentId);
        if (Objects.nonNull(departmentNode)) {
            tmpDepartmentAccessIds.add(departmentNode.getId());
            tmpEmployeeAccessIds.addAll(departmentNode.getChildrenEmployeesIds());
            initDepartmentIdsRecursive(departmentNode, tmpDepartmentAccessIds, tmpEmployeeAccessIds);
        }
    }

    private void initDepartmentIdsRecursive(DepartmentNode departmentNode, Set<Long> tmpDepartmentAccessIds, Set<Long> tmpEmployeeAccessIds) {
        tmpDepartmentAccessIds.addAll(departmentNode.getChildrenDepartmentsIds());
        tmpEmployeeAccessIds.addAll(departmentNode.getChildrenEmployeesIds());
        for (DepartmentNode children : departmentNode.getChildrens()) {
            initDepartmentIdsRecursive(children, tmpDepartmentAccessIds, tmpEmployeeAccessIds);
        }
    }


    private EmployeeDepartmentTree feelTree(QueryTransaction transaction) throws PlatformException {
        final EmployeeDepartmentTree tree = new EmployeeDepartmentTree(new DepartmentNode(null, null, "root"));
        final DepartmentNode rootNode = tree.getRoot();
        feelTreeNodes(tree, rootNode, transaction);
        return tree;
    }

    boolean feelTreeNodes(EmployeeDepartmentTree tree, DepartmentNode parent, QueryTransaction transaction) throws PlatformException {
        tree.getDepartmentElementsCache().put(parent.id, parent);
        final ArrayList<DepartmentReadable> nodes = departmentReadableResource
                .getAll(new HashFilter(DepartmentReadable.FIELD_PARENT_ID, parent.getId()), transaction);
        for (DepartmentReadable node : nodes) {
            final DepartmentNode departmentNode = new DepartmentNode(node.getId(), parent.getId(), node.getName());
            try (IteratorEntity<EmployeeReadable> iterator = employeeReadableResource.findAll(new HashFilter(EmployeeReadable.FIELD_DEPARTMENT_ID, node.getId()), transaction)) {
                while (iterator.hasNext()) {
                    final EmployeeReadable employeeReadable = iterator.next();
                    departmentNode.addChildrenEmployeeId(employeeReadable.getId());
                }
            }
            parent.addChildren(departmentNode);
            final boolean result = feelTreeNodes(tree, departmentNode, transaction);
            if (!result) {
                return false;
            }
        }
        return true;
    }


    private static class EmployeeDepartmentTree {

        private final DepartmentNode root;
        Map<Long, DepartmentNode> departmentElementsCache = new HashMap<>();

        public EmployeeDepartmentTree(DepartmentNode root) {
            this.root = root;
        }

        public DepartmentNode getRoot() {
            return root;
        }

        public Map<Long, DepartmentNode> getDepartmentElementsCache() {
            return departmentElementsCache;
        }
    }

    public static class DepartmentNode {
        private final Long id;
        private final Long parentId;
        private final String name;
        private final Set<Long> childrenDepartmentsIds = new HashSet<>();
        private final Set<Long> childrenEmployeesIds = new HashSet<>();
        private final Set<DepartmentNode> childrens = new HashSet<>();

        public DepartmentNode(Long id, Long parentId, String name) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }

        public void addChildren(DepartmentNode children) {
            this.childrens.add(children);
            this.childrenDepartmentsIds.add(children.getId());
        }

        public void addChildrenEmployeeId(Long childrenEmployeeId) {
            this.childrenEmployeesIds.add(childrenEmployeeId);
        }

        public Long getId() {
            return id;
        }

        public Set<DepartmentNode> getChildrens() {
            return childrens;
        }

        public Set<Long> getChildrenDepartmentsIds() {
            return childrenDepartmentsIds;
        }

        public Set<Long> getChildrenEmployeesIds() {
            return childrenEmployeesIds;
        }

        public Long getParentId() {
            return parentId;
        }

        public String getName() {
            return name;
        }
    }

    private long setAccessRoles(EmployeeEntity.Builder builder, long employeeReadableId, QueryTransaction transaction) throws PlatformException {
        long accessRolesFieldStart = System.currentTimeMillis();
        if (fieldHandler.isAccessRoleIds()) {
            employeeAccessRoleReadableResource.forEach(
                    new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeReadableId),
                    employeeAccessRole -> builder.addAccessRoleIds(employeeAccessRole.getAccessRoleId()),
                    transaction);

        }
        return System.currentTimeMillis() - accessRolesFieldStart;
    }

    private long setMonitoringFields(EmployeeEntity.Builder builder, long employeeReadableId, ContextTransactionImpl contextTransaction) throws PlatformException {
        long monitoringFieldStart = System.currentTimeMillis();
        if (fieldHandler.isMonitoringType()) {
            employeeMonitoringGetter.exec(getter -> builder.setMonitoringType(getter.getEmployeeMonitoringType(employeeReadableId, contextTransaction).name()));
        }
        if (fieldHandler.isEmployeeAccountIds() || fieldHandler.isAccountDomains() || fieldHandler.isAccountLogins()) {
            employeeAccountGetter.exec(
                    getter -> {
                        Collection<EmployeeAccountMetaData> employeeAccountMetaData = getter.get(employeeReadableId, contextTransaction);
                        if (Objects.nonNull(employeeAccountMetaData)) {
                            employeeAccountMetaData.forEach(o -> {
                                if (fieldHandler.isEmployeeAccountIds()) {
                                    builder.addEmployeeAccountId(o.id());
                                }
                                if (fieldHandler.isAccountDomains()) {
                                    builder.addEmployeeAccountDomain(StringUtils.defaultString(o.domain()));
                                }
                                if (fieldHandler.isAccountLogins()) {
                                    builder.addEmployeeAccountLogin(StringUtils.defaultString(o.login()));
                                }
                            });
                        }
                    });
        }
        return System.currentTimeMillis() - monitoringFieldStart;
    }

    private long setPhones(EmployeeEntity.Builder builder, long employeeReadableId, QueryTransaction transaction) throws PlatformException {
        long phoneFieldStart = System.currentTimeMillis();
        if (fieldHandler.isPhones()) {
            final ArrayList<EmployeePhoneReadable> phoneReadables = employeePhoneReadableResource.getAll(
                    new HashFilter(EmployeePhoneReadable.FIELD_EMPLOYEE_ID, employeeReadableId),
                    transaction
            );
            for (EmployeePhoneReadable phoneReadable : phoneReadables) {
                builder.addPhone(phoneReadable.getPhoneNumber());
            }
        }
        return System.currentTimeMillis() - phoneFieldStart;
    }

    private void setAuthenticationAssigned(EmployeeEntity.Builder builder, long employeeReadableId, QueryTransaction transaction) throws PlatformException {
        if (fieldHandler.isAuthenticationAssigned()) {
            builder.setAuthenticationAssigned(
                    authenticationChecker.isAnyAuthenticationAssigned(
                            employeeReadableId,
                            new ContextTransactionImpl(new SourceSystemImpl(), transaction))
            );
        }
    }

    private void setBaseFields(EmployeeEntity.Builder builder, EmployeeReadable employeeReadable) {

        builder.setId(employeeReadable.getId());

        if (fieldHandler.isFirstName()) {
            builder.setFirstName(employeeReadable.getFirstName());
        }

        if (fieldHandler.isSecondName()) {
            builder.setSecondName(employeeReadable.getSecondName());
        }

        if (fieldHandler.isPatronymic()) {
            builder.setPatronymic(employeeReadable.getPatronymic());
        }

        if (fieldHandler.isLogin()) {
            builder.setLogin(employeeReadable.getLogin());
        }

        if (fieldHandler.isEmail()) {
            builder.setEmail(employeeReadable.getEmail());
        }

        if (fieldHandler.isName()) {
            builder.setName(employeeReadable.getDisplayName(displayNameFormat));
        }

        if (fieldHandler.isPersonnelNumber()) {
            builder.setPersonnelNumber(employeeReadable.getPersonnelNumber());
        }
    }

    public static class DepartmentInfo {
        private final Long id;
        private final String name;
        private final Long parentId;

        public DepartmentInfo(Long id, String name, Long parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Long getParentId() {
            return parentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DepartmentInfo that = (DepartmentInfo) o;

            if (id != that.id) return false;
            if (parentId != that.parentId) return false;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (int) (parentId ^ (parentId >>> 32));
            return result;
        }
    }

    private Set<Long> getAllEmployeeIds(QueryTransaction transaction) throws PlatformException {
        Set<Long> employeeIds = new HashSet<>();
        employeeReadableResource.forEach(employeeReadable -> employeeIds.add(employeeReadable.getId()), transaction);
        return employeeIds;
    }

    private Set<Long> getAllDepartmentIds(QueryTransaction transaction) throws PlatformException {
        Set<Long> departmentIds = new HashSet<>();
        departmentReadableResource.forEach(departmentReadable -> departmentIds.add(departmentReadable.getId()), transaction);
        return departmentIds;
    }

    private String getFieldValue(long fieldId, long employeeId, ContextTransaction context) throws PlatformException {
        AdditionalFieldReadable field = additionalFieldReadableResource.get(fieldId, context.getTransaction());
        switch (field.getDataType()) {
            case STRING:
                Optional<String> sValue =
                        rcAdditionalFieldValueGetter.getStringValue(fieldId, employeeId, 0, context);
                return StringUtils.defaultString(sValue.get());
            case STRING_ARRAY:
                JSONArray jsonArray = new JSONArray();
                ArrayList<String> stringArray = rcAdditionalFieldValueGetter.getStringArray(fieldId, employeeId, context);
                stringArray.stream()
                        .map(StringUtils::defaultString)
                        .forEach(jsonArray::appendElement);
                return jsonArray.toJSONString();
            case LONG:
                Optional<Long> lValue =
                        rcAdditionalFieldValueGetter.getLongValue(fieldId, employeeId, 0, context);
                return String.valueOf(lValue.get()).replace("null", "");
            case LONG_ARRAY:
                jsonArray = new JSONArray();
                ArrayList<Long> longArray = rcAdditionalFieldValueGetter.getLongArray(fieldId, employeeId, context);
                longArray.stream()
                        .map(String::valueOf)
                        .map(str -> str.replace("null", ""))
                        .forEach(jsonArray::appendElement);
                return jsonArray.toJSONString();
            case DATE:
                Optional<LocalDate> dValue =
                        rcAdditionalFieldValueGetter.getDateValue(fieldId, employeeId, 0, context);
                return dValue.isPresent() && Objects.nonNull(dValue.get()) ? dValue.get().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
            case DATE_ARRAY:
                jsonArray = new JSONArray();
                ArrayList<LocalDate> dateArray = rcAdditionalFieldValueGetter.getDateArray(fieldId, employeeId, context);
                dateArray.stream()
                        .map(o -> Objects.nonNull(o) ? o.format(DateTimeFormatter.ISO_LOCAL_DATE) : "")
                        .forEach(jsonArray::appendElement);
                return jsonArray.toJSONString();
            case DATETIME:
                Optional<Instant> dtValue =
                        rcAdditionalFieldValueGetter.getDateTimeValue(fieldId, employeeId, 0, context);
                return dtValue.isPresent() && Objects.nonNull(dtValue.get()) ?
                        LocalDateTime.ofInstant(dtValue.get(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) :
                        "";
            case DATETIME_ARRAY:
                DateTimeFormatter dateTime64Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                jsonArray = new JSONArray();
                ArrayList<Instant> dateTimeArray = rcAdditionalFieldValueGetter.getDateTimeArray(fieldId, employeeId, context);
                dateTimeArray.stream()
                        .map(o -> Objects.nonNull(o) ? LocalDateTime.ofInstant(o, ZoneId.systemDefault()).format(dateTime64Formatter) : "")
                        .forEach(jsonArray::appendElement);
                return jsonArray.toJSONString();
            case ID:
                Optional<Long> idValue =
                        rcAdditionalFieldValueGetter.getIdValue(fieldId, employeeId, 0, context);

                if (!idValue.isPresent()) {
                    return "";
                }

                if (idValue.get() == null) {
                    return "";
                }

                if (Objects.equals(field.getListSource(), EmployeeReadable.class.getName())) {
                    final EmployeeReadable employeeReadable = employeeReadableResource.get(idValue.get(), context.getTransaction());
                    if (employeeReadable != null) {
                        return employeeReadable.getDisplayName(displayNameFormat);
                    }
                } else if (Objects.equals(field.getListSource(), DepartmentReadable.class.getName())) {
                    final DepartmentReadable departmentReadable = departmentReadableResource.get(idValue.get(), context.getTransaction());
                    if (departmentReadable != null) {
                        return departmentReadable.getName();
                    }
                }
                break;
            default:
        }
        return "";
    }
}
