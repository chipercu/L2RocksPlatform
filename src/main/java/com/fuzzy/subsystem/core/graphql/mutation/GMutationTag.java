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
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.graphql.query.tag.GTag;
import com.fuzzy.subsystem.core.remote.tag.RCTag;
import com.fuzzy.subsystem.core.remote.tag.TagBuilder;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

@GraphQLTypeOutObject("mutation_tag")
public class GMutationTag {

	private static final String ID = "id";
	private static final String IDS = "ids";
	private static final String NAME = "name";
	private static final String COLOUR = "colour";

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Создание тега")
	public static GraphQLQuery<RemoteObject, GTag> create(
			@NonNull @GraphQLName(NAME)
			@GraphQLDescription("Название")
			final String name,
			@NonNull @GraphQLName(COLOUR)
			@GraphQLDescription("Цвет")
			final String colour
	) {
		GraphQLQuery<RemoteObject, GTag> query = new GraphQLQuery<RemoteObject, GTag>() {

			private RCTag rcTagControl;

			@Override
			public void prepare(ResourceProvider resources) {
				rcTagControl = resources.getQueryRemoteController(CoreSubsystem.class, RCTag.class);
			}

			@Override
			public GTag execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				TagReadable tag = rcTagControl.create(
						new TagBuilder()
								.withName(name)
								.withColour(colour)
								.withReadOnly(false),
						context);
				return tag != null ? new GTag(tag) : null;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.TAG_SETTINGS, AccessOperation.CREATE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Обновление тега")
	public static GraphQLQuery<RemoteObject, GTag> update(
			@NonNull @GraphQLName(ID)
			@GraphQLDescription("Идентификатор обновляемого тега")
			final long tagId,
			@GraphQLName(NAME)
			@GraphQLDescription("Новое значение названия")
			final GOptional<String> name,
			@GraphQLName(COLOUR)
			@GraphQLDescription("Новое значение цвета")
			final GOptional<String> colour
	) {
		GraphQLQuery<RemoteObject, GTag> query = new GraphQLQuery<RemoteObject, GTag>() {

			private RCTag rcTagControl;

			@Override
			public void prepare(ResourceProvider resources) {
				rcTagControl = resources.getQueryRemoteController(CoreSubsystem.class, RCTag.class);
			}

			@Override
			public GTag execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				TagBuilder tagBuilder = new TagBuilder();
				if (name.isPresent()) {
					tagBuilder.withName(name.get());
				}
				if (colour.isPresent()) {
					tagBuilder.withColour(colour.get());
				}
				TagReadable tag = rcTagControl.update(tagId, tagBuilder, context);
				return tag != null ? new GTag(tag) : null;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.TAG_SETTINGS, AccessOperation.WRITE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Удаление тегов")
	public static GraphQLQuery<RemoteObject, HashSet<Long>> remove(
			@NonNull @GraphQLName(IDS)
			@GraphQLDescription("Идентификаторы удаляемых тегов")
			final HashSet<Long> tagIds
	) {
		GraphQLQuery<RemoteObject, HashSet<Long>> query =  new GraphQLQuery<RemoteObject, HashSet<Long>>() {

			private RCTag rcTagControl;

			@Override
			public void prepare(ResourceProvider resources) {
				rcTagControl = resources.getQueryRemoteController(CoreSubsystem.class, RCTag.class);
			}

			@Override
			public HashSet<Long> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				return rcTagControl.remove(tagIds, context);
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.TAG_SETTINGS, AccessOperation.DELETE);
	}
}
