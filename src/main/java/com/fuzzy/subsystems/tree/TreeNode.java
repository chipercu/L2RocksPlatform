package com.fuzzy.subsystems.tree;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.SorterComparator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TreeNode<Node extends DomainObject, Item extends DomainObject> extends TreeElement implements Identifiable<Long> {

    public static class Paging {
        private Integer limit;
        private Set<Long> alwaysComingNodes;
        private Set<Long> alwaysComingItems;

        public Paging limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Paging alwaysComingData(GInputNodesItems alwaysComingData) {
            if (alwaysComingData != null) {
                this.alwaysComingNodes = alwaysComingData.getNodes();
                this.alwaysComingItems = alwaysComingData.getItems();
            } else {
                this.alwaysComingNodes = null;
                this.alwaysComingItems = null;
            }
            return this;
        }
    }

    private final Node node;
    private final NodeItemSorter<Long, Long, TreeNode<Node, Item>, TreeItem<Item>> elements;
    private final List<TreeNode<Node, Item>> hiddenChildNodes;
    private final List<TreeItem<Item>> hiddenChildItems;
    private boolean selectedInRest = false;

    public TreeNode(
            Node node,
            SorterComparator<Node> nodeComparator,
            SorterComparator<Item> itemComparator,
            Paging paging
    ) {
        this.node = node;
        this.elements = new NowNodeItemSorter.Builder<Long, Long, TreeNode<Node, Item>, TreeItem<Item>>(
                (o1, o2) -> nodeComparator != null ? nodeComparator.compare(o1.getNode(), o2.getNode()) : 0,
                (o1, o2) -> itemComparator != null ? itemComparator.compare(o1.getItem(), o2.getItem()) : 0)
                .limit(paging.limit)
                .alwaysComingNodes(paging.alwaysComingNodes)
                .alwaysComingItems(paging.alwaysComingItems)
                .onNodeMovedToRestFunction(treeNode -> {
                    if (treeNode.isSelected()) {
                        selectedInRest = true;
                    }
                })
                .onItemMovedToRestFunction(treeItem -> {
                    if (treeItem.isSelected()) {
                        selectedInRest = true;
                    }
                }).build();
        this.hiddenChildNodes = new ArrayList <>();
        this.hiddenChildItems = new ArrayList <>();
    }

    @Override
    public @NonNull Long getIdentifier() {
        return node.getId();
    }

    public Node getNode() {
        return node;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public void addChildNode(TreeNode<Node, Item> node, boolean hidden) throws PlatformException {
        if (hidden) {
            hiddenChildNodes.add(node);
        } else {
            elements.addNode(node);
        }
    }

    public void addChildItem(TreeItem<Item> item, boolean hidden) throws PlatformException {
        if (hidden) {
            hiddenChildItems.add(item);
        } else {
            elements.addItem(item);
        }
    }

    public void finishAdditionChildItems() throws PlatformException {
        elements.finish();
    }

    public boolean isSelectedInRest() {
        return selectedInRest;
    }

    public List <TreeNode <Node, Item>> getChildNodes() {
        return elements.getNodes();
    }

    public List <TreeItem <Item>> getChildItems() {
        return elements.getItems();
    }

    public int getChildItemsNextCount() {
        return elements.getNextCount();
    }

    public List <TreeNode <Node, Item>> getHiddenChildNodes() {
        return hiddenChildNodes;
    }

    public List <TreeItem<Item>> getHiddenChildItems() {
        return hiddenChildItems;
    }
}
