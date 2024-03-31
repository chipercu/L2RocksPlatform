package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;

@GraphQLTypeInput("items_filter")
public class GItemsFilter {

	private final GFilterOperation operation;
	private final HashSet<Long> items;

	public GItemsFilter(
			@GraphQLDescription("Действие")
			@NonNull @GraphQLName("operation") GFilterOperation operation,
			@GraphQLDescription("Идентификаторы элементов")
			@Nullable @GraphQLName("items") HashSet<Long> items) {
		this.operation = operation;
		this.items = items;
	}

	public GFilterOperation getOperation() {
		return operation;
	}

	public HashSet<Long> getItems() {
		return items;
	}
}
