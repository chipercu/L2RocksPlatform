package com.fuzzy.database.domainobject.filter;

import com.fuzzy.database.domainobject.filter.Filter;

public class EmptyFilter implements Filter {

    public static final EmptyFilter INSTANCE = new EmptyFilter();

    private EmptyFilter() {}
}
