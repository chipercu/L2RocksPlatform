package com.fuzzy.subsystem.core.tree.department;

import com.fuzzy.subsystems.graphql.input.GInputItems;
import com.fuzzy.subsystems.graphql.input.GTreePaging;

import java.util.HashSet;

public class DepartmentTreeParam {

    public Long authEmployeeId = null;
    public String textFilter = null;
    public GInputItems idFilter = null;
    public GTreePaging paging = null;
    public GInputItems alwaysComingData = null;
    public HashSet<Long> topNodes = null;
}
