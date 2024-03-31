package com.fuzzy.subsystem.core.textfilter;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Collections;
import java.util.Set;

public class TagTextFilterGetter extends DomainObjectTextFilterGetter<TagReadable> {

	public static final Set<Integer> FIELD_NAMES = Collections.singleton(TagReadable.FIELD_NAME);

	public TagTextFilterGetter(ResourceProvider resources) {
		super(
				resources.getReadableResource(TagReadable.class),
				FIELD_NAMES
		);
	}
}