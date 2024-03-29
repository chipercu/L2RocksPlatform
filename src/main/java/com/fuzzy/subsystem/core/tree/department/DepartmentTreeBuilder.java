package com.fuzzy.subsystem.core.tree.department;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.tree.GTreeView;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystem.core.grouping.enumerator.EmployeeGroupingEnumerator;
import com.fuzzy.subsystem.core.textfilter.DepartmentTextFilterGetter;
import com.fuzzy.subsystem.core.tree.employee.EmployeeTreeChecker;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.tree.Tree;
import com.fuzzy.subsystems.tree.TreeBuilder;
import com.fuzzy.subsystems.tree.TreeParam;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.util.HashSet;

public class DepartmentTreeBuilder {

    private final TreeBuilder<DepartmentReadable, EmployeeReadable> treeBuilder;
    private final EmployeeTreeChecker checker;

    private DepartmentTreeBuilder(
            EmployeeTreeChecker checker,
            ResourceProvider resources
    ) {
        this.treeBuilder = new TreeBuilder<>(
                resources.getReadableResource(DepartmentReadable.class),
                null,
                new DepartmentGroupingEnumerator(resources),
                new EmployeeGroupingEnumerator(resources),
                new DepartmentTextFilterGetter(resources),
                null,
                checker
        );
        this.treeBuilder.setNodeComparator(
                (o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName())
        );
        this.checker = checker;
    }

    public DepartmentTreeBuilder(ResourceProvider resources) {
        this(new EmployeeTreeChecker(resources), resources);
    }

    public Tree<DepartmentReadable, EmployeeReadable> build(DepartmentTreeParam param, QueryTransaction transaction)
            throws PlatformException {
        if (param.authEmployeeId != null) {
            checker.setAuthEmployeeId(param.authEmployeeId);
        }
        TreeParam treeParam = new TreeParam();

        treeParam.topElements = new GInputNodesItems(param.topNodes, null);
        treeParam.textFilter = param.textFilter;
        if (param.idFilter != null && param.idFilter.isSpecified()) {
            treeParam.idFilter = new GInputNodesItems(param.idFilter.getItems(), null);
        }
        treeParam.paging = param.paging;
        if (param.alwaysComingData != null && param.alwaysComingData.isSpecified()) {
            treeParam.alwaysComingData = new GInputNodesItems(param.alwaysComingData.getItems(), null);
            if (param.authEmployeeId != null) {
                HashSet<Long> departments = treeParam.alwaysComingData.getNodes();
                for (Long departmentId : departments) {
                    if (!checker.ensureAccess(transaction).checkDepartment(departmentId)) {
                        departments.remove(departmentId);
                    }
                }
            }
        }
        treeParam.view = GTreeView.NODE;
        return this.treeBuilder.build(treeParam, transaction);
    }
}



