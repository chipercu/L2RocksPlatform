package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigSetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.config.FirstDayOfWeek;
import com.fuzzy.subsystem.core.graphql.query.config.GQueryAppConfigCore;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

@GraphQLTypeOutObject("mutation_app_config_core")
public class GMutationAppConfigCore {

	private static final String DISPLAY_NAME_FORMAT = "display_name_format";
	private static final String FIRST_DAY_OF_WEEK = "first_day_of_week";

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Обновление конфигурации системы")
	public static GraphQLQuery<RemoteObject, Class<GQueryAppConfigCore>> update(
			@GraphQLName(FIRST_DAY_OF_WEEK)
			@GraphQLDescription("Новое значение первого дня недели")
			final GOptional<FirstDayOfWeek> firstDayOfWeek,
			@GraphQLName(DISPLAY_NAME_FORMAT)
			@GraphQLDescription("Новое значение формата отображаемого имени сотрудника")
			final GOptional<DisplayNameFormat> displayNameFormat
	) {
		GraphQLQuery<RemoteObject, Class<GQueryAppConfigCore>> query =
				new GraphQLQuery<RemoteObject, Class<GQueryAppConfigCore>>() {

			private CoreConfigSetter coreConfigSetter;

			@Override
			public void prepare(ResourceProvider resources) {
				coreConfigSetter = new CoreConfigSetter(resources);
			}

			@Override
			public Class<GQueryAppConfigCore> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				if (firstDayOfWeek.isPresent()) {
					if (firstDayOfWeek.get() == null) {
						throw GeneralExceptionBuilder.buildEmptyValueException(FIRST_DAY_OF_WEEK);
					}
					coreConfigSetter.set(
							CoreConfigDescription.FIRST_DAY_OF_WEEK, firstDayOfWeek.get(), context);
				}
				if (displayNameFormat.isPresent()) {
					if (displayNameFormat.get() == null) {
						throw GeneralExceptionBuilder.buildEmptyValueException(DISPLAY_NAME_FORMAT);
					}
					coreConfigSetter.set(
							CoreConfigDescription.DISPLAY_NAME_FORMAT, displayNameFormat.get(), context);
				}
				return GQueryAppConfigCore.class;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.WRITE);
	}
}
