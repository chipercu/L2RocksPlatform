package com.fuzzy.subsystem.core.graphql.query;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.accessrole.GQueryAccessRole;
import com.fuzzy.subsystem.core.graphql.query.apikey.GQueryApiKey;
import com.fuzzy.subsystem.core.graphql.query.authentication.GQueryAuthentication;
import com.fuzzy.subsystem.core.graphql.query.config.GQueryAppConfig;
import com.fuzzy.subsystem.core.graphql.query.config.GQueryServer;
import com.fuzzy.subsystem.core.graphql.query.department.GQueryDepartment;
import com.fuzzy.subsystem.core.graphql.query.depersonalization.GQueryDepersonalization;
import com.fuzzy.subsystem.core.graphql.query.employee.GQueryEmployee;
import com.fuzzy.subsystem.core.graphql.query.field.GQueryField;
import com.fuzzy.subsystem.core.graphql.query.filter.GQueryFilter;
import com.fuzzy.subsystem.core.graphql.query.license.GQueryLicense;
import com.fuzzy.subsystem.core.graphql.query.systemevent.GQuerySystemEvent;
import com.fuzzy.subsystem.core.graphql.query.tag.GQueryTag;
import com.fuzzy.subsystem.core.graphql.query.timezone.GTimeZone;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Set;

@GraphQLTypeOutObject("query")
public class GQuery {

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с сервером")
	public static Class<GQueryServer> getServer() {
		return GQueryServer.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с конфигурацими")
	public static Class<GQueryAppConfig> getAppConfig() {
		return GQueryAppConfig.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с сотрудниками")
	public static Class<GQueryEmployee> getEmployee() {
		return GQueryEmployee.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с ролями доступа")
	public static Class<GQueryAccessRole> getAccessRole() {
		return GQueryAccessRole.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с ключами API")
	public static Class<GQueryApiKey> getApiKey() {
		return GQueryApiKey.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с отделами")
	public static Class<GQueryDepartment> getDepartment() {
		return GQueryDepartment.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с тегами")
	public static Class<GQueryTag> getTag() {
		return GQueryTag.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с фильтрами")
	public static Class<GQueryFilter> getFilter() {
		return GQueryFilter.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с лицензиями")
	public static Class<GQueryLicense> getLicense() {
		return GQueryLicense.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с полями")
	public static Class<GQueryField> getField() {
		return GQueryField.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с копированием обезличенной базы данных")
	public static Class<GQueryDepersonalization> getDepersonalization() {
		return GQueryDepersonalization.class;
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Доступные часовые зоны")
	public static GraphQLQuery<RemoteObject, ArrayList<GTimeZone>> getAvailableTimeZones() {
		return new GraphQLQuery<>() {

			private LanguageGetter languageGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				languageGetter = new LanguageGetter(resources);
			}

			@Override
			public ArrayList<GTimeZone> execute(RemoteObject source, ContextTransactionRequest context)
					throws PlatformException {
				class TZPair {
					final GTimeZone gTimeZone;
					final ZoneOffset zoneOffset;

					TZPair(GTimeZone gTimeZone, ZoneOffset zoneOffset) {
						this.gTimeZone = gTimeZone;
						this.zoneOffset = zoneOffset;
					}
				}
				LocalDateTime localDateTime = LocalDateTime.now();
				Language language = languageGetter.get(context);
				Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
				ArrayList<TZPair> availableTimeZones = new ArrayList<>(availableZoneIds.size());
				for (String timeZone : availableZoneIds) {
					availableTimeZones.add(new TZPair(
							new GTimeZone(timeZone, language),
							localDateTime.atZone(ZoneId.of(timeZone)).getOffset()
					));
				}
				availableTimeZones.sort((o1, o2) -> {
					int res = o2.zoneOffset.compareTo(o1.zoneOffset);
					if (res == 0) {
						res = o1.gTimeZone.getDisplayName().compareTo(o2.gTimeZone.getDisplayName());
					}
					return res;
				});
				ArrayList<GTimeZone> gAvailableTimeZones = new ArrayList<>(availableTimeZones.size());
				for (TZPair tzPair : availableTimeZones) {
					gAvailableTimeZones.add(tzPair.gTimeZone);
				}
				return gAvailableTimeZones;
			}
		};
	}

	@GraphQLField
	@GraphQLDescription("Запросы, связанные с аутентификациями")
	public static Class<GQueryAuthentication> getAuthentication() {
		return GQueryAuthentication.class;
	}

	@GraphQLField
	@GraphQLDescription("Запросы, системных уведомлений")
	public static Class<GQuerySystemEvent> getSystemEvents() {
		return GQuerySystemEvent.class;
	}
}
