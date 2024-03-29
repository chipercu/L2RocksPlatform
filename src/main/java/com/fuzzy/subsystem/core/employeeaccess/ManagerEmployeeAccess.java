package com.fuzzy.subsystem.core.employeeaccess;

import com.fuzzy.subsystems.entityelements.Elements;

public class ManagerEmployeeAccess {

    private Elements elements;

    public ManagerEmployeeAccess(Elements elements) {
        this.elements = elements;
    }

    public boolean checkDepartment(Long departmentId) {
        return elements.isAll || elements.nodes.contains(departmentId);
    }

    public boolean checkEmployee(Long employeeId) {
        return elements.isAll || elements.items.contains(employeeId);
    }

    public boolean isAll() {
        return elements.isAll;
    }

    public Elements getElements() {
        return elements;
    }
}
