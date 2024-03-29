package com.fuzzy.subsystems.tree;

import com.infomaximum.database.domainobject.DomainObject;

import java.io.Serializable;

public class Tree<Node extends DomainObject, Item extends DomainObject> implements Serializable {

    private TreeNode<Node, Item> root;
    private int matchCount = 0;

    public Tree(TreeNode <Node, Item> root) {
        this.root = root;
    }

    public TreeNode <Node, Item> getRoot() {
        return root;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void matchCountIncrement() {
        matchCount++;
    }
}
