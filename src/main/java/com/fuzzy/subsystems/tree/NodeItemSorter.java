package com.fuzzy.subsystems.tree;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.remote.Identifiable;

import java.util.List;

public interface NodeItemSorter<N, I, Node extends Identifiable<N>, Item extends Identifiable<I>> {

	void addNode(Node node) throws PlatformException;

	void addItem(Item item) throws PlatformException;

	void finish() throws PlatformException;

	void clear() throws PlatformException;

	boolean isEmpty();

	List<Node> getNodes();

	List<Item> getItems();

	int getNextCount();
}
