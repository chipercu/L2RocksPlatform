package com.fuzzy.subsystem.core.remote.tag;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;

import java.util.HashSet;

public interface RCTag extends QueryRemoteController {

	TagReadable create(TagBuilder tagBuilder, ContextTransaction context) throws PlatformException;

	TagReadable update(final long tagId, TagBuilder tagBuilder, ContextTransaction context) throws PlatformException;

	HashSet<Long> remove(final HashSet<Long> tagIds, ContextTransaction context) throws PlatformException;
}