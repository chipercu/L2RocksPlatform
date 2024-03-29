package com.fuzzy.subsystems.tree;

import com.google.common.collect.Sets;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.iterator.Iterator;
import com.fuzzy.subsystem.core.graphql.query.tree.GTreeView;
import com.fuzzy.subsystems.comparators.DomainObjectIdComparator;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.graphql.input.GTreePaging;
import com.fuzzy.subsystems.grouping.GroupingEnumerator;
import com.fuzzy.subsystems.grouping.NodeGrouping;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.textfilter.TextFilterGetter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreeBuilder<Node extends DomainObject, Item extends DomainObject> {

    private static class TTree<Node extends DomainObject, Item extends DomainObject> extends Tree<Node, Item> {

        private final Map<Long, TreeNode<Node, Item>> nodes = new HashMap<>();
        private final Set<Long> items = new HashSet<>();

        public TTree(TreeNode<Node, Item> root) {
            super(root);
        }

        public Map<Long, TreeNode<Node, Item>> getNodes() {
            return nodes;
        }

        public Set<Long> getItems() {
            return items;
        }
    }

    private final ReadableResource<Node> nodeReadableResource;
    private final ReadableResource<Item> itemReadableResource;
    private final NodeGrouping nodeGrouping;
    private final NodeItemGrouping itemGrouping;
    private final TextFilterGetter<Node> nodeTextFilterGetter;
    private final TextFilterGetter<Item> itemTextFilterGetter;
    private SorterComparator<Node> nodeComparator;
    private SorterComparator<Item> itemComparator;
    private final TreeChecker<Node, Item> checker;

    private static class Filter {

        public Set<Long> nodes = null;
        public Set<Long> items = null;

        public boolean isAll() {
            return nodes == null && items == null;
        }

        void retainAll(Filter filter) {
            if (isAll()) {
                nodes = filter.nodes;
                items = filter.items;
            } else if (!filter.isAll()) {
                if (nodes != null && filter.nodes != null) {
                    nodes.retainAll(filter.nodes);
                } else {
                    nodes = new HashSet<>();
                }
                if (items != null && filter.items != null) {
                    items.retainAll(filter.items);
                } else {
                    items = new HashSet<>();
                }
            }
        }
    }

    public TreeBuilder(
            ReadableResource<Node> nodeReadableResource,
            ReadableResource<Item> itemReadableResource,
            GroupingEnumerator nodeGroupingEnumerator,
            GroupingEnumerator itemGroupingEnumerator,
            TextFilterGetter<Node> nodeTextFilterGetter,
            TextFilterGetter<Item> itemTextFilterGetter,
            TreeChecker<Node, Item> checker
    ) {
        this.nodeReadableResource = nodeReadableResource;
        this.itemReadableResource = itemReadableResource;
        nodeGrouping = new NodeGrouping(nodeGroupingEnumerator);
        itemGrouping = itemGroupingEnumerator != null ?
                new NodeItemGrouping(nodeGroupingEnumerator, itemGroupingEnumerator) : null;
        this.nodeTextFilterGetter = nodeTextFilterGetter;
        this.itemTextFilterGetter = itemTextFilterGetter;
        this.nodeComparator = null;
        this.itemComparator = null;
        this.checker = checker;
    }


    private static class Comparator<T extends DomainObject> implements SorterComparator<T> {

        private final SorterComparator<T> comparator;
        private final HashSet<Long> topElements;

        Comparator(SorterComparator<T> comparator, HashSet<Long> topItems) {
            this.comparator = comparator;
            this.topElements = topItems;
        }

        @Override
        public int compare(T o1, T o2) throws PlatformException {
            if (topElements.contains(o1.getId())) {
                return topElements.contains(o2.getId()) ? comparator.compare(o1, o2) : -1;
            }
            return topElements.contains(o2.getId()) ? 1 : comparator.compare(o1, o2);
        }
    }


    public void setNodeComparator(SorterComparator<Node> nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    public void setItemComparator(SorterComparator<Item> itemComparator) {
        this.itemComparator = itemComparator;
    }

    public Tree<Node, Item> build(TreeParam param, QueryTransaction transaction) throws PlatformException {
        HashSet<Long> textFilterNodes = getIdsByTextFilter(nodeTextFilterGetter, param.textFilter, transaction);
        HashSet<Long> textFilterItems = null;
        if (param.view == GTreeView.NODE_ITEM) {
            textFilterItems = getIdsByTextFilter(itemTextFilterGetter, param.textFilter, transaction);
        }
        GInputNodesItems textFilter = new GInputNodesItems(textFilterNodes, textFilterItems);
        Filter filter = createFilter(param.idFilter, param.view, false, transaction);
        filter.retainAll(createFilter(textFilter, param.view, false, transaction));
        GInputNodesItems rootFilter = null;
        if (param.rootFilter != null) {
            HashSet<Long> rootFilterNodes = param.rootFilter.getNodes() == null ?
                    null : new HashSet<>(param.rootFilter.getNodes());
            HashSet<Long> rootFilterItems = param.rootFilter.getItems() == null ?
                    null : new HashSet<>(param.rootFilter.getItems());
            rootFilter = new GInputNodesItems(rootFilterNodes, rootFilterItems);
            filter.retainAll(createFilter(rootFilter, param.view, true, transaction));
        }
        HashSet<Long> topElementsNodes = param.topElements.isSpecifiedNodes() ? param.topElements.getNodes() : new HashSet<>();
        HashSet<Long> topElementsItems = param.topElements.isSpecifiedItems() ? param.topElements.getItems() : new HashSet<>();
        param.topElements = new GInputNodesItems(topElementsNodes, topElementsItems);
        HashSet<Long> nodes = Sets.newHashSet(param.topElements.getNodes());
        for (Long item : param.topElements.getItems()) {
            itemGrouping.forEachParentOfItemRecursively(item, transaction, nodes::add);
        }
        for (Long node : param.topElements.getNodes()) {
            itemGrouping.forEachParentOfNodeRecursively(node, transaction, nodes::add);
        }
        param.topElements.getNodes().addAll(nodes);

        TTree<Node, Item> tree = new TTree<>(new TreeNode<>(
                null,
                new Comparator<>(
                        this.nodeComparator != null ? this.nodeComparator : new DomainObjectIdComparator<>(),
                        param.topElements.getNodes()),
                new Comparator<>(
                        this.itemComparator != null ? this.itemComparator : new DomainObjectIdComparator<>(),
                        param.topElements.getItems()),
                new TreeNode.Paging()
                        .limit(param.paging != null ? param.paging.getLimit(null) : null)
                        .alwaysComingData(param.alwaysComingData)
        ));
        addNodesToTree(
                tree,
                filter,
                rootFilter,
                textFilter,
                param.paging,
                param.alwaysComingData,
                param.topElements,
                transaction
        );
        if (param.view == GTreeView.NODE_ITEM) {
            addItemsToTree(
                    tree,
                    filter,
                    rootFilter,
                    textFilter,
                    param.paging,
                    param.alwaysComingData,
                    param.topElements,
                    transaction
            );
        }
        if (param.alwaysComingData != null) {
            addHiddenNodesToTree(
                    tree,
                    rootFilter,
                    textFilter,
                    param.paging,
                    param.alwaysComingData,
                    param.topElements,
                    transaction
            );
            if (param.view == GTreeView.NODE_ITEM) {
                addHiddenItemsToTree(
                        tree,
                        rootFilter,
                        textFilter,
                        param.paging,
                        param.alwaysComingData,
                        param.topElements,
                        transaction);
            }
        }
        tree.getRoot().finishAdditionChildItems();
        for (Map.Entry<Long, TreeNode<Node, Item>> node : tree.getNodes().entrySet()) {
            node.getValue().finishAdditionChildItems();
        }
        return tree;
    }

    private static <T extends DomainObject> HashSet<Long> getIdsByTextFilter(
            TextFilterGetter<T> textFilterGetter, String text, QueryTransaction transaction) throws PlatformException {
        HashSet<Long> result = null;
        if (textFilterGetter != null && !StringUtils.isEmpty(text)) {
            result = new HashSet<>();
            try (Iterator<T> ie = textFilterGetter.findAll(text, transaction)) {
                while (ie.hasNext()) {
                    result.add(ie.next().getId());
                }
            }
        }
        return result;
    }

    private Filter createFilter(
            GInputNodesItems inputFilter, GTreeView view, boolean processInputFilter, QueryTransaction transaction)
            throws PlatformException {
        Filter outputFilter = new Filter();
        if (inputFilter != null) {
            if (inputFilter.getNodes() != null) {
                outputFilter.nodes = nodeGrouping.getChildrenRecursively(inputFilter.getNodes(), transaction);
                if (processInputFilter) {
                    inputFilter.getNodes().removeAll(outputFilter.nodes);
                }
                outputFilter.nodes.addAll(inputFilter.getNodes());
            }
            if (view == GTreeView.NODE_ITEM) {
                if (outputFilter.nodes != null) {
                    outputFilter.items = itemGrouping.getChildItemsRecursively(outputFilter.nodes, transaction);
                }
                if (inputFilter.getItems() != null) {
                    if (outputFilter.items != null) {
                        if (processInputFilter) {
                            inputFilter.getItems().removeAll(outputFilter.items);
                        }
                        outputFilter.items.addAll(inputFilter.getItems());
                    } else {
                        outputFilter.items = new HashSet<>(inputFilter.getItems());
                    }
                }
            }
        }
        return outputFilter;
    }

    private void addNodesToTree(
            TTree<Node, Item> tree,
            Filter filter,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction
    ) throws PlatformException {
        if (filter.isAll()) {
            try (IteratorEntity<Node> ie = nodeReadableResource.iterator(transaction)) {
                while (ie.hasNext()) {
                    Node node = ie.next();
                    if (checker == null || checker.checkNode(node, transaction)) {
                        addNodeToTree(
                                tree,
                                node,
                                false,
                                rootFilter,
                                textFilter,
                                paging,
                                alwaysComingData,
                                topElements,
                                transaction
                        );
                    }
                }
            }
        } else if (filter.nodes != null) {
            for (Long nodeId : filter.nodes) {
                Node node = nodeReadableResource.get(nodeId, transaction);
                if (checker == null || checker.checkNode(node, transaction)) {
                    addNodeToTree(
                            tree,
                            node,
                            false,
                            rootFilter,
                            textFilter,
                            paging,
                            alwaysComingData,
                            topElements,
                            transaction
                    );
                }
            }
        }
    }

    private void addHiddenNodesToTree(
            TTree<Node, Item> tree,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction

    ) throws PlatformException {
        if (alwaysComingData.getNodes() == null) {
            return;
        }
        for (Long nodeId : alwaysComingData.getNodes()) {
            if (!tree.getNodes().containsKey(nodeId)) {
                Node node = nodeReadableResource.get(nodeId, transaction);
                addNodeToTree(tree, node, true, rootFilter, textFilter, paging, alwaysComingData, topElements, transaction);
            }
        }
    }

    private TreeNode<Node, Item> addNodeToTree(
            TTree<Node, Item> tree,
            Node node,
            boolean hidden,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction
    ) throws PlatformException {
        Long nodeId = node.getId();
        TreeNode<Node, Item> treeNode = tree.getNodes().get(nodeId);
        if (treeNode != null) {
            return treeNode;
        }
        treeNode = new TreeNode<>(
                node,
                new Comparator<>(
                        this.nodeComparator != null ? this.nodeComparator : new DomainObjectIdComparator<>(),
                        topElements.getNodes()),
                new Comparator<>(
                        this.itemComparator != null ? this.itemComparator : new DomainObjectIdComparator<>(),
                        topElements.getItems()),
                new TreeNode.Paging()
                        .limit(paging != null ? paging.getLimit(nodeId) : null)
                        .alwaysComingData(alwaysComingData)
        );
        if (textFilter.getNodes() != null && textFilter.getNodes().contains(nodeId)) {
            tree.matchCountIncrement();
            treeNode.setSelected(true);
        }
        tree.getNodes().put(nodeId, treeNode);

        Long parentNodeId = null;
        if (rootFilter == null || rootFilter.getNodes() == null || !rootFilter.getNodes().contains(nodeId)) {
            Long[] parentNodes = { null };
            nodeGrouping.forEachParent(nodeId, transaction, groupingParentNodeId -> {
                parentNodes[0] = groupingParentNodeId;
                return false;
            });
            parentNodeId = parentNodes[0];
        }

        if (parentNodeId == null) {
            tree.getRoot().addChildNode(treeNode, hidden);
        } else {
            TreeNode<Node, Item> parentTreeNode = tree.getNodes().get(parentNodeId);
            if (parentTreeNode != null) {
                parentTreeNode.addChildNode(treeNode, hidden);
            } else {
                Node parentNode = nodeReadableResource.get(parentNodeId, transaction);
                addNodeToTree(tree, parentNode, hidden, rootFilter, textFilter, paging, alwaysComingData, topElements, transaction)
                        .addChildNode(treeNode, hidden);
            }
        }
        return treeNode;
    }

    private void addItemsToTree(
            TTree<Node, Item> tree,
            Filter filter,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction
    ) throws PlatformException {
        if (filter.isAll()) {
            try (IteratorEntity<Item> ie = itemReadableResource.iterator(transaction)) {
                while (ie.hasNext()) {
                    Item item = ie.next();
                    if (checker == null || checker.checkItem(item, transaction)) {
                        addItemToTree(
                                tree,
                                item,
                                false,
                                rootFilter,
                                textFilter,
                                paging,
                                alwaysComingData,
                                topElements,
                                transaction
                        );
                    }
                }
            }
        } else if (filter.items != null) {
            for (Long itemId : filter.items) {
                Item item = itemReadableResource.get(itemId, transaction);
                if (checker == null || checker.checkItem(item, transaction)) {
                    addItemToTree(
                            tree,
                            item,
                            false,
                            rootFilter,
                            textFilter,
                            paging,
                            alwaysComingData,
                            topElements,
                            transaction
                    );
                }
            }
        }
    }

    private void addHiddenItemsToTree(
            TTree<Node, Item> tree,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction
    ) throws PlatformException {
        if (alwaysComingData.getItems() == null) {
            return;
        }
        for (Long itemId : alwaysComingData.getItems()) {
            if (!tree.getItems().contains(itemId)) {
                Item item = itemReadableResource.get(itemId, transaction);
                addItemToTree(tree, item, true, rootFilter, textFilter, paging, alwaysComingData, topElements, transaction);
            }
        }
    }

    private void addItemToTree(
            TTree<Node, Item> tree,
            Item item,
            boolean hidden,
            GInputNodesItems rootFilter,
            GInputNodesItems textFilter,
            GTreePaging paging,
            GInputNodesItems alwaysComingData,
            GInputNodesItems topElements,
            QueryTransaction transaction
    ) throws PlatformException {

        Long parentNodeId = null;
        if (rootFilter == null || rootFilter.getItems() == null || !rootFilter.getItems().contains(item.getId())) {
            Long[] parentNodes = { null };
            itemGrouping.forEachParentOfItem(item.getId(), transaction, groupingParentNodeId -> {
                parentNodes[0] = groupingParentNodeId;
                return false;
            });
            parentNodeId = parentNodes[0];
        }

        TreeItem<Item> treeItem = new TreeItem<>(item);
        if (textFilter.getItems() != null && textFilter.getItems().contains(item.getId())) {
            tree.matchCountIncrement();
            treeItem.setSelected(true);
        }
        tree.getItems().add(item.getId());
        if (parentNodeId == null) {
            tree.getRoot().addChildItem(treeItem, hidden);
        } else {
            TreeNode<Node, Item> treeNode = tree.getNodes().get(parentNodeId);
            if (treeNode != null) {
                treeNode.addChildItem(treeItem, hidden);
            } else {
                Node parentNode = nodeReadableResource.get(parentNodeId, transaction);
                addNodeToTree(tree, parentNode, hidden, rootFilter, textFilter, paging, alwaysComingData, topElements, transaction)
                        .addChildItem(treeItem, hidden);
            }
        }
    }
}
