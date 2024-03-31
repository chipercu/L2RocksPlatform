package com.fuzzy.subsystems.tree;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;

public interface TreeChecker<Node extends DomainObject, Item extends DomainObject> {

    boolean checkNode(Node node, QueryTransaction transaction) throws PlatformException;

    boolean checkItem(Item item, QueryTransaction transaction) throws PlatformException;
}
