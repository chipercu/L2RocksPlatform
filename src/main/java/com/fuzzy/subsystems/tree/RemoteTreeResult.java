package com.fuzzy.subsystems.tree;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;

import java.util.ArrayList;
import java.util.Collection;

public abstract class RemoteTreeResult<Y extends RemoteTreeElement<?> & RemoteObject> implements RemoteObject {

    private static final String FIELD_ITEMS = "elements";
    private static final String MATCH_COUNT = "match_count";

    private ArrayList<Y> elements = null;
    private int matchCount = 0;

    protected RemoteTreeResult() {
    }

    public <Node extends DomainObject, Item extends DomainObject> RemoteTreeResult(Tree<Node, Item> tree) throws PlatformException {
        this.elements = new ArrayList<>();
        this.matchCount = tree.getMatchCount();
        addNodeToElements(tree.getRoot(), new ArrayList <>(), false);
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public ArrayList<Y> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Y> elements) {
        this.elements = elements;
    }

    public abstract Y createElement(Object source) throws PlatformException;

    private <Node extends DomainObject, Item extends DomainObject> void addNodeToElements(
            TreeNode<Node, Item> node,
            ArrayList<Long> parents,
            boolean hidden
    ) throws PlatformException {
        ArrayList<Long> innerParents = new ArrayList<>(parents);
        if (node.getNode() != null) {
            Y element = createElement(node.getNode());
            element.setHidden(hidden);
            element.setSelected(node.isSelected());
            element.setParents(parents);
            elements.add(element);
            innerParents.add(node.getNode().getId());
        }
        for (TreeNode<Node, Item> childNode : node.getChildNodes()) {
            addNodeToElements(childNode, innerParents, false);
        }
        for (TreeNode<Node, Item> hiddenChildNode : node.getHiddenChildNodes()) {
            addNodeToElements(hiddenChildNode, innerParents, true);
        }
        addItemsToElements(node.getChildItems(), innerParents, false);
        addItemsToElements(node.getHiddenChildItems(), innerParents, true);
        if (node.getChildItemsHasNext()) {
            Y element = createElement(null);
            element.setNext(node.getChildItemsHasNext());
            element.setSelected(node.isSelectedInRest());
            element.setParents(innerParents);
            elements.add(element);
        }
    }

    private <Item extends DomainObject> void addItemsToElements(
            Collection<TreeItem<Item>> items,
            ArrayList<Long> parents,
            boolean hidden
    ) throws PlatformException {
        for (TreeItem<Item> item : items) {
            Y element = createElement(item.getItem());
            element.setHidden(hidden);
            element.setSelected(item.isSelected());
            element.setParents(parents);
            elements.add(element);
        }
    }
}
