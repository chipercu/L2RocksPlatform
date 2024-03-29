package com.fuzzy.subsystem.core.graphql.query.tag;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.autocomplete.TagAutocomplete;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.graphql.query.tag.autocomplete.GTagAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.tag.list.GTagListResult;
import com.fuzzy.subsystem.core.list.TagListBuilder;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQueryImpl;
import com.fuzzy.subsystems.list.ListParam;
import com.fuzzy.subsystems.utils.ComparatorUtility;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

@GraphQLTypeOutObject("tag_query")
public class GQueryTag {

    private static final String ID = "id";
    private static final String TEXT_FILTER = "text_filter";
    private static final String EXCLUDED_TAGS = "excluded_tags";
    private static final String PAGING = "paging";
    private static final String TOP_TAGS = "top_tags";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тег по идентификатору")
    public static GraphQLQuery<RemoteObject, GTag> getTag(
            @NonNull
            @GraphQLName(ID)
            @GraphQLDescription("Идентификатор тега")
            final Long id
    ) {
        return new GPrimaryKeyQueryImpl<>(TagReadable.class, GTag::new, id);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Теги с учетом фильтров и пагинацией")
    public static GraphQLQuery<RemoteObject, GTagListResult> getTagList(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER)
            final GTextFilter textFilter,
            @GraphQLDescription("Теги, отображающиеся в начале списка")
            @GraphQLName(TOP_TAGS)
            final GOptional<HashSet<Long>> topTags,
            @GraphQLName(PAGING)
            @GraphQLDescription("Параметры пейджинга")
            final GPaging paging
    ) {
        return new GraphQLQuery<>() {

            private TagListBuilder tagBuilder;

            @Override
            public void prepare(ResourceProvider resources) {
                tagBuilder = new TagListBuilder(resources);
                tagBuilder.setComparator((o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName()));
            }

            @Override
            public GTagListResult execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                HashSet<Long> tops = topTags != null && topTags.isPresent() ? topTags.get() : null;
                ListParam<Long> params = new ListParam.Builder<Long>()
                        .withTextFilter(textFilter)
                        .withPaging(paging)
                        .withTopItems(tops)
                        .build();
                return new GTagListResult(tagBuilder.build(params, context));
            }
        };
    }

    @GraphQLField(value = "tag_autocomplete")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по тегам")
    public static GraphQLQuery<RemoteObject, GTagAutocompleteResult> getTagAutocomplete(
            @GraphQLName(TEXT_FILTER)
            @GraphQLDescription("Текстовый фильтр")
            final GTextFilter textFilter,
            @GraphQLName(EXCLUDED_TAGS)
            @GraphQLDescription("Идентификаторы исключаемых из выдачи тегов")
            final HashSet<Long> excludedTags,
            @GraphQLName(PAGING)
            @GraphQLDescription("Параметры пейджинга")
            final GPaging paging
    ) {
        return new GraphQLQuery<>() {

            private ReadableResource<TagReadable> tagReadableResource;
            private TagAutocomplete tagAutocomplete;

            @Override
            public void prepare(ResourceProvider resources) {
                tagReadableResource = resources.getReadableResource(TagReadable.class);
                tagAutocomplete = new TagAutocomplete(resources);
            }

            @Override
            public GTagAutocompleteResult execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                HashSet<Long> validExcludedTags = new PrimaryKeyValidator(true).validate(
                        excludedTags, tagReadableResource, context.getTransaction());
                return new GTagAutocompleteResult(
                        tagAutocomplete.execute(textFilter, validExcludedTags, paging, context));
            }
        };
    }
}
