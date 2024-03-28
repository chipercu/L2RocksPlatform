package com.fuzzy.subsystems.tree;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public interface TreeChecker<Node extends DomainObject, Item extends DomainObject> {

    boolean checkNode(Node node, QueryTransaction transaction) throws PlatformException;

    boolean checkItem(Item item, QueryTransaction transaction) throws PlatformException;
}
