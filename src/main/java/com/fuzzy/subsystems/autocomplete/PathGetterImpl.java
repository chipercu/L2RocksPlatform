package com.fuzzy.subsystems.autocomplete;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystems.grouping.GroupingEnumerator;
import com.fuzzy.subsystems.grouping.NodeGrouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PathGetterImpl<T extends DomainObject, PathItem extends DomainObject> implements PathGetter<T> {

    private final int parentFieldNumber;
    private final int pathDisplayFieldNumber;
    private final Set<Integer> loadingFields;
    private final NodeGrouping pathGrouping;
    private final ReadableResource<PathItem> pathReadableResource;

    public PathGetterImpl(
            ResourceProvider resources,
            int parentFieldNumber,
            int pathDisplayFieldNumber,
            GroupingEnumerator pathGroupingEnumerator,
            Class<PathItem> pathClazz) {
        this.parentFieldNumber = parentFieldNumber;
        this.pathDisplayFieldNumber = pathDisplayFieldNumber;
        this.loadingFields = Collections.singleton(pathDisplayFieldNumber);
        this.pathGrouping = new NodeGrouping(pathGroupingEnumerator);
        this.pathReadableResource = resources.getReadableResource(pathClazz);
    }

    @Override
    public List<String> getPath(T object, QueryTransaction transaction) throws PlatformException {
        Long parentId = object.get(parentFieldNumber);
        List<String> path = new ArrayList<>();
        if (parentId != null) {
            PathItem pathItem = pathReadableResource.get(parentId, loadingFields, transaction);
            if (pathItem != null) {
                path.add(pathItem.get(pathDisplayFieldNumber));
            }
            pathGrouping.forEachParentRecursively(parentId, transaction, currentParentId -> {
                PathItem currentPathItem = pathReadableResource.get(currentParentId, loadingFields, transaction);
                if (currentPathItem != null) {
                    path.add(currentPathItem.get(pathDisplayFieldNumber));
                }
                return true;
            });
            Collections.reverse(path);
        }
        return  path;
    }
}
