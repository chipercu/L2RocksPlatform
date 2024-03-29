package com.fuzzy.subsystem.core.tree.employee;

import com.fuzzy.subsystems.graphql.enums.EnableMonitoringType;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.graphql.input.GItemsFilter;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.input.GTreePaging;

import java.util.ArrayList;

public class EmployeeTreeParam {

    public Long authEmployeeId;
    public ArrayList<EnableMonitoringType> monitoringTypes;
    public GItemsFilter accessRoleFilter;
    public GItemsFilter authenticationFilter;
    public GStandardFilter idFilter;
    public String textFilter;
    public GTreePaging paging;
    public GInputNodesItems alwaysComingData;
    public GInputNodesItems topElements;
}