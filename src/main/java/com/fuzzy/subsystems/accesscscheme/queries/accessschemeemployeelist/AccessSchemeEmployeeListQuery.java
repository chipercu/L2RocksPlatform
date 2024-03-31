package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.utils.EmployeeComparator;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.accesscscheme.GAccessSchemeOperation;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.localization.GlobalLocalization;
import com.fuzzy.subsystems.accesscscheme.localization.Localization;
import com.fuzzy.subsystems.function.BiFunction;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.graphql.enums.SortingDirection;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.list.ListItem;
import com.fuzzy.subsystems.list.ListParam;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.readableresourcecache.ReadableResourceCache;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.utils.Cache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AccessSchemeEmployeeListQuery<
        K extends Comparable<K>,
        S extends RemoteObject & Identifiable<?>,
        T extends Identifiable<K> & AccessSchemeItem<Long, ?, ? extends GAccessSchemeOperation>> extends GraphQLQuery<S, GAccessSchemeEmployeeListResult> {

    private final GTextFilter textFilter;
    private final GPaging paging;
    private final AccessSchemeEmployeeSortingColumn sortingColumn;
    private final SortingDirection sortingDirection;

    private final Cache<Long, String> departmentCache;
    private final Cache<String, String> accessOperationCache;

    private final Localization localization;
    private ReadableResourceCache<EmployeeReadable> employeeReadableResource;
    private ReadableResourceCache<DepartmentReadable> departmentReadableResource;
    private AccessSchemeWorkspaceListBuilderImpl listBuilder;
    private CoreConfigGetter coreConfigGetter;
    private LanguageGetter languageGetter;

    private BiFunction<T, ContextTransaction, Boolean> checker;

    public AccessSchemeEmployeeListQuery(@Nullable GTextFilter textFilter,
                                         @Nullable GPaging paging,
                                         @NonNull AccessSchemeEmployeeSortingColumn sortingColumn,
                                         @NonNull SortingDirection sortingDirection,
                                         @NonNull Localization localization) {
        this.textFilter = textFilter;
        this.paging = paging;
        this.sortingColumn = sortingColumn;
        this.sortingDirection = sortingDirection;
        this.localization = localization;
        departmentCache = new Cache<>();
        accessOperationCache = new Cache<>();
    }

    public AccessSchemeEmployeeListQuery(@NonNull Component component,
                                         @Nullable GTextFilter textFilter,
                                         @Nullable GPaging paging,
                                         @NonNull AccessSchemeEmployeeSortingColumn sortingColumn,
                                         @NonNull SortingDirection sortingDirection) {
        this(textFilter, paging, sortingColumn, sortingDirection, new GlobalLocalization(component));
    }

    public void setChecker(BiFunction<T, ContextTransaction, Boolean> checker) {
        this.checker = checker;
    }

    @Override
    public void prepare(@NonNull ResourceProvider resources) {
        employeeReadableResource = new ReadableResourceCache<>(resources, EmployeeReadable.class);
        departmentReadableResource = new ReadableResourceCache<>(resources, DepartmentReadable.class);
        listBuilder = new AccessSchemeWorkspaceListBuilderImpl(resources);
        coreConfigGetter = new CoreConfigGetter(resources);
        languageGetter = new LanguageGetter(resources);
    }

    @Override
    public GAccessSchemeEmployeeListResult execute(@NonNull S source,
                                                   @NonNull ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        listBuilder.setSource(source);
        validateAccessToSource(source, context);
        Language language = languageGetter.get(context);
        SorterComparator<T> comparator = getComparator(language, transaction).direction(sortingDirection);
        listBuilder.setComparator(comparator);
        ListParam<K> listParam = new ListParam.Builder<K>()
                .withTextFilter(textFilter)
                .withPaging(paging)
                .build();
        ListResult<T> listResult = listBuilder.build(listParam, context);
        ArrayList<GAccessSchemeEmployeeListItem> gItems = new ArrayList<>(listResult.items().size());
        for (ListItem<T> item : listResult.items()) {
            EmployeeReadable employee = employeeReadableResource.get(item.item().getSubjectId(), transaction);
            GEmployee gEmployee = new GEmployee(employee);
            GDepartment gDepartment = employee.getDepartmentId() != null ?
                    new GDepartment(departmentReadableResource.get(employee.getDepartmentId(), transaction)) : null;
            GAccessSchemeEmployee gAccessSchemeEmployee = new GAccessSchemeEmployee(
                    gEmployee, gDepartment, item.item().getOperation(), getAccessOperationLoc(item.item(), language));
            GAccessSchemeEmployeeListItem gItem = new GAccessSchemeEmployeeListItem();
            gItem.setElement(gAccessSchemeEmployee);
            gItem.setSelected(item.selected());
            gItem.setHidden(item.hidden());
            gItems.add(gItem);
        }
        return new GAccessSchemeEmployeeListResult(gItems, listResult.matchCount(), listResult.hasNext());
    }

    protected abstract void forEachAccessSchemeItem(@NonNull S source,
                                                    @NonNull Consumer<T> handler,
                                                    @NonNull ContextTransaction<?> context) throws PlatformException;

    protected abstract void validateAccessToSource(@NonNull S source,
                                                   @NonNull ContextTransactionRequest context) throws PlatformException;

    private SorterComparator<T> getComparator(Language language,
                                              QueryTransaction transaction) throws PlatformException {

        return switch (sortingColumn) {
            case EMPLOYEE -> getComparator(List.of(
                            AccessSchemeEmployeeSortingColumn.EMPLOYEE,
                            AccessSchemeEmployeeSortingColumn.DEPARTMENT,
                            AccessSchemeEmployeeSortingColumn.ACCESS_OPERATION),
                    language, transaction);
            case DEPARTMENT -> getComparator(List.of(
                            AccessSchemeEmployeeSortingColumn.DEPARTMENT,
                            AccessSchemeEmployeeSortingColumn.EMPLOYEE,
                            AccessSchemeEmployeeSortingColumn.ACCESS_OPERATION),
                    language, transaction);
            case ACCESS_OPERATION -> getComparator(List.of(
                            AccessSchemeEmployeeSortingColumn.ACCESS_OPERATION,
                            AccessSchemeEmployeeSortingColumn.EMPLOYEE,
                            AccessSchemeEmployeeSortingColumn.DEPARTMENT),
                    language, transaction);
        };
    }

    private SorterComparator<T> getComparator(Collection<AccessSchemeEmployeeSortingColumn> sortingColumns,
                                              Language language,
                                              QueryTransaction transaction) throws PlatformException {
        SorterComparator<T> comparator = null;
        for (AccessSchemeEmployeeSortingColumn sortingColumn : sortingColumns) {
            SorterComparator<T> nextComparator = null;
            switch (sortingColumn) {
                case EMPLOYEE -> {
                    DisplayNameFormat displayNameFormat =
                            coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, transaction);
                    EmployeeComparator employeeComparator = new EmployeeComparator(displayNameFormat);
                    nextComparator = (o1, o2) -> employeeComparator.compare(
                            employeeReadableResource.get(o1.getSubjectId(), transaction),
                            employeeReadableResource.get(o2.getSubjectId(), transaction));
                }
                case DEPARTMENT -> nextComparator = (o1, o2) -> getDepartmentFullName(o1.getSubjectId(), transaction)
                        .compareTo(getDepartmentFullName(o2.getSubjectId(), transaction));
                case ACCESS_OPERATION -> nextComparator = (o1, o2) -> getAccessOperationLoc(o1, language)
                        .compareTo(getAccessOperationLoc(o2, language));
            }
            comparator = comparator == null ? nextComparator : comparator.thenComparing(nextComparator);
        }
        return comparator;
    }

    private String getDepartmentFullName(long employeeId, QueryTransaction transaction) throws PlatformException {
        EmployeeReadable employee = employeeReadableResource.get(employeeId, transaction);
        if (employee.getDepartmentId() == null) {
            return "";
        }
        return departmentCache.get(employee.getDepartmentId(), depId -> {
            StringBuilder fullNameBuilder = new StringBuilder();
            Long currentDepartmentId = depId;
            while (currentDepartmentId != null)  {
                DepartmentReadable department = departmentReadableResource.get(currentDepartmentId, transaction);
                fullNameBuilder.append(department.getName());
                currentDepartmentId = department.getParentDepartmentId();
            }
            return fullNameBuilder.toString();
        });
    }

    private String getAccessOperationLoc(T accessSchemeItem, Language language) throws PlatformException {
        return accessOperationCache.get(accessSchemeItem.getOperation().getLocKey(),
                locKey -> localization.getLocalization(locKey, language));
    }

    private class AccessSchemeWorkspaceListBuilderImpl extends AccessSchemeEmployeeListBuilder<K, T> {

        private S source;

        public AccessSchemeWorkspaceListBuilderImpl(ResourceProvider resources) {
            super(resources);
        }

        public void setSource(S source) {
            this.source = source;
        }

        @Override
        protected void forEach(@NonNull Consumer<T> handler, @NonNull ContextTransaction<?> context) throws PlatformException {
            forEachAccessSchemeItem(source, handler, context);
        }

        @Override
        protected boolean checkItem(@NonNull T item, @NonNull ContextTransaction<?> context) throws PlatformException {
            if (checker != null) {
                return checker.apply(item, context);
            }
            return super.checkItem(item, context);
        }
    }
}
