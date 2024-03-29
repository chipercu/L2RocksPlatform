package com.fuzzy.subsystems.tree;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;

public interface TreeChecker<Node extends DomainObject, Item extends DomainObject> {

    boolean checkNode(Node node, QueryTransaction transaction) throws PlatformException;

    boolean checkItem(Item item, QueryTransaction transaction) throws PlatformException;
}
