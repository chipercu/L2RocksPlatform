package com.fuzzy.subsystem.core.textfilter;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Collections;
import java.util.Set;

public class ApiKeyTextFilterGetter extends DomainObjectTextFilterGetter<ApiKeyReadable> {

	public static final Set<Integer> FIELD_NAME = Collections.singleton(ApiKeyReadable.FIELD_NAME);

	public ApiKeyTextFilterGetter(ResourceProvider resource) {
		super(
				resource.getReadableResource(ApiKeyReadable.class),
				FIELD_NAME
		);
	}
}
