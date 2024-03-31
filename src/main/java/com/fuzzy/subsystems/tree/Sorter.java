package com.fuzzy.subsystems.tree;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.BinarySearch;
import com.fuzzy.subsystems.sorter.SorterComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Sorter<K, T extends Identifiable<K>> {

	private final List<T> data;
	private boolean hasNext;

	private final SorterComparator<T> comparator;
	private final Set<K> alwaysComingIds;
	private final Consumer<T> onMovedToRestFunction;

	public static class Builder<K, T extends Identifiable<K>> {
		private final SorterComparator<T> comparator;

		private Consumer<T> onMovedToRestFunction = null;
		private Set<K> alwaysComingIds = null;

		public Builder(SorterComparator<T> comparator) {
			this.comparator = comparator;
		}

		public Builder<K, T> onMovedToRestFunction(Consumer<T> value) {
			this.onMovedToRestFunction = value;
			return this;
		}

		public Builder<K, T> alwaysComingIds(Set<K> value) {
			this.alwaysComingIds = value;
			return this;
		}

		public Sorter<K, T> build() {
			return new Sorter<>(this);
		}
	}

	private Sorter(Builder<K, T> builder) {
		data = new ArrayList<>();
		comparator = builder.comparator;
		onMovedToRestFunction = builder.onMovedToRestFunction;
		alwaysComingIds = builder.alwaysComingIds;
	}

	public void add(T object, Integer limit) throws PlatformException {
		int index = BinarySearch.findPosition(data, object, comparator);
		if (limit == null) {
			data.add(index, object);
		} else if (index < limit) {
			data.add(index, object);
			moveLastVisibleToRest(limit);
		} else if (alwaysComingIds != null && alwaysComingIds.contains(object.getIdentifier())) {
			data.add(index, object);
		} else {
			moveToRest(object);
		}
	}

	public void moveLastVisibleToRest(int limit) {
		if (data.size() <= limit) {
			return;
		}
		T object = data.get(limit);
		if (alwaysComingIds == null || !alwaysComingIds.contains(object.getIdentifier())) {
			data.remove(limit);
			moveToRest(object);
		}
	}

	public void clear() {
		data.clear();
		hasNext = false;
	}

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public List<T> getData() {
		return data;
	}

	public boolean hasNext() {
		return hasNext;
	}

	private void moveToRest(T object) {
		if (onMovedToRestFunction != null) {
			onMovedToRestFunction.accept(object);
		}
		hasNext = true;
	}
}
