package com.fuzzy.subsystems.graphql.input;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;

@GraphQLTypeInput("input_items")
public class GInputItems {

    private HashSet<Long> items;

    public GInputItems(
            @GraphQLDescription("Идентификаторы элементов")
            @Nullable @GraphQLName("items") HashSet<Long> items) {
        this.items = items;
    }

    public @Nullable HashSet<Long> getItems() {
        return items;
    }

    public boolean isSpecified() {
        return items != null;
    }

    public <T extends DomainObject> void validate(@NonNull ReadableResource<T> itemResource,
                                                  @NonNull QueryTransaction transaction) throws PlatformException {
        if (isSpecified()) {
            items = new PrimaryKeyValidator(true).validate(items, itemResource, transaction);
        }
    }
}
