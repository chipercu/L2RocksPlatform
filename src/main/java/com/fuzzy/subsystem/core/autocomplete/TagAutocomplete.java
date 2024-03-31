package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.textfilter.TagTextFilterGetter;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;

public class TagAutocomplete extends LightAutocomplete<TagReadable> {

	public TagAutocomplete(ResourceProvider resources) {
		super(new AtomicAutocompleteImpl<>(
				new TagTextFilterGetter(resources),
				TagTextFilterGetter.FIELD_NAMES,
				null
		));
	}
}