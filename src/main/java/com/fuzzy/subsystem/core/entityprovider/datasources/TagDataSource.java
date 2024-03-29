package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.infomaximum.database.domainobject.filter.IdFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.entityprovider.entity.TagEntity;
import com.infomaximum.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

import java.util.ArrayList;
import java.util.List;

public class TagDataSource implements DataSourceProvider<TagEntity> {

    private ReadableResource<TagReadable> tagReadableResource;

    @Override
    public void prepare(ResourceProvider resources) {
        tagReadableResource = resources.getReadableResource(TagReadable.class);
    }

    @Override
    public DataSourceIterator<TagEntity> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException {
        List<TagEntity> tagEntities = new ArrayList<>();
        try (IteratorEntity<TagReadable> iterator = tagReadableResource.findAll(new IdFilter(lastProcessedId + 1, Long.MAX_VALUE), transaction)) {
            while (iterator.hasNext() && tagEntities.size() < limit) {
                TagReadable tagReadable = iterator.next();
                tagEntities.add(new TagEntity(tagReadable));
            }
        }
        return new BaseSourceIterator<>(tagEntities);
    }
}
