package com.fuzzy.subsystem.core.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.textfilter.TagTextFilterGetter;
import com.fuzzy.subsystems.list.ListBuilder;
import com.fuzzy.subsystems.list.ListChecker;
import com.fuzzy.subsystems.list.ListParam;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.sorter.SorterComparator;

public class TagListBuilder {

    private final ReadableResource<TagReadable> tagResource;
    private ListChecker<TagReadable> checker;
    private final TagTextFilterGetter textFilterGetter;
    private SorterComparator<TagReadable> tagComparator;


    public TagListBuilder(ResourceProvider resources) {
        this.tagResource = resources.getReadableResource(TagReadable.class);
        this.textFilterGetter = new TagTextFilterGetter(resources);
    }

    public void setComparator(SorterComparator<TagReadable> tagComparator) {
        this.tagComparator = tagComparator;
    }

    public void setChecker(ListChecker<TagReadable> checker) {
        this.checker = checker;
    }

    public ListResult<TagReadable> build(ListParam<Long> param, ContextTransaction<?> context)
            throws PlatformException {
        ListBuilder<TagReadable> builder = new ListBuilder<>(
                tagResource,
                textFilterGetter,
                checker);
        builder.setComparator(tagComparator);
        return builder.build(param, context);
    }
}
