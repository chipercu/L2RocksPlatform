package com.fuzzy.subsystems.tree;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.SorterComparator;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class NowNodeItemSorter<N, I, Node extends Identifiable<N>, Item extends Identifiable<I>> implements NodeItemSorter<N, I, Node, Item> {

	private final Sorter<N, Node> nodes;
	private final Sorter<I, Item> items;
	private final Integer limit;

	public static class Builder<N, I, Node extends Identifiable<N>, Item extends Identifiable<I>> {

		private final Sorter.Builder<N, Node> nodesBuilder;
		private final Sorter.Builder<I, Item> itemsBuilder;
		private Integer limit;

		public Builder(SorterComparator<Node> nodeComparator, SorterComparator<Item> itemComparator) {
			nodesBuilder = new Sorter.Builder<>(nodeComparator);
			itemsBuilder = new Sorter.Builder<>(itemComparator);
		}

		public Builder<N, I, Node, Item> onNodeMovedToRestFunction(Consumer<Node> value) {
			nodesBuilder.onMovedToRestFunction(value);
			return this;
		}

		public Builder<N, I, Node, Item> onItemMovedToRestFunction(Consumer<Item> value) {
			itemsBuilder.onMovedToRestFunction(value);
			return this;
		}

		public Builder<N, I, Node, Item> limit(Integer value) {
			this.limit = value;
			return this;
		}

		public Builder<N, I, Node, Item> alwaysComingNodes(Set<N> value) {
			nodesBuilder.alwaysComingIds(value);
			return this;
		}

		public Builder<N, I, Node, Item> alwaysComingItems(Set<I> value) {
			itemsBuilder.alwaysComingIds(value);
			return this;
		}

		public NowNodeItemSorter<N, I, Node, Item> build() {
			return new NowNodeItemSorter<>(this);
		}
	}

	private NowNodeItemSorter(Builder<N, I, Node, Item> builder) {
		nodes = builder.nodesBuilder.build();
		items = builder.itemsBuilder.build();
		limit = builder.limit;
	}

	@Override
	public void addNode(Node node) throws PlatformException {
		nodes.add(node, limit);
		Integer itemsLimit = getItemsLimit();
		if (itemsLimit != null) {
			items.moveLastVisibleToRest(itemsLimit);
		}
	}

	@Override
	public void addItem(Item item) throws PlatformException {
		items.add(item, getItemsLimit());
	}

	@Override
	public void finish() {
		// do nothing
	}

	@Override
	public void clear() {
		nodes.clear();
		items.clear();
	}

	@Override
	public boolean isEmpty() {
		return nodes.isEmpty() && items.isEmpty();
	}

	@Override
	public List<Node> getNodes() {
		return nodes.getData();
	}

	@Override
	public List<Item> getItems() {
		return items.getData();
	}

	@Override
	public boolean hasNext() {
		return nodes.hasNext() || items.hasNext();
	}

	private Integer getItemsLimit() {
		return limit != null ? Math.max(limit - nodes.size(), 0) : null;
	}
}
