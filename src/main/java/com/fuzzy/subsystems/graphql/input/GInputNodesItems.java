package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;

@GraphQLTypeInput("input_nodes_items")
public class GInputNodesItems {

	private HashSet<Long> nodes;
	private HashSet<Long> items;

	public GInputNodesItems(
			@GraphQLDescription("Идентификаторы групп")
			@Nullable @GraphQLName("nodes") HashSet<Long> nodes,
			@GraphQLDescription("Идентификаторы элементов")
			@Nullable @GraphQLName("items") HashSet<Long> items) {
		this.nodes = nodes;
		this.items = items;
	}

	public HashSet<Long> getNodes() {
		return nodes;
	}

	public HashSet<Long> getItems() {
		return items;
	}

	public boolean isSpecifiedNodes() {
		return nodes != null;
	}

	public boolean isSpecifiedItems() {
		return items != null;
	}

	public boolean isSpecified() {
		return isSpecifiedNodes() || isSpecifiedItems();
	}

	public <T extends DomainObject, Y extends DomainObject> void validate(@NonNull ReadableResource<T> nodeResource,
																		  @NonNull ReadableResource<Y> itemResource,
																		  @NonNull QueryTransaction transaction) throws PlatformException {
		PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(true);
		if (isSpecifiedNodes()) {
			nodes = primaryKeyValidator.validate(nodes, nodeResource, transaction);
		}
		if (isSpecifiedItems()) {
			items = primaryKeyValidator.validate(items, itemResource, transaction);
		}
	}
}