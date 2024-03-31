package com.fuzzy.subsystem.core.graphql.query.timezone;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.utils.TimeZoneUtils;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.time.ZoneId;

@GraphQLTypeOutObject("time_zone")
public class GTimeZone implements RemoteObject {

	private final String timeZoneId;
	private final String displayName;

	public GTimeZone(ZoneId timeZoneId, Language language) {
		this.timeZoneId = timeZoneId.getId();
		this.displayName = TimeZoneUtils.getDisplayName(timeZoneId, language);
	}

	public GTimeZone(String timeZoneId, Language language) {
		this.timeZoneId = timeZoneId;
		this.displayName = TimeZoneUtils.getDisplayName(timeZoneId, language);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Идентификатор")
	public String getId() {
		return timeZoneId;
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Наименование")
	public String getDisplayName() {
		return displayName;
	}

}
