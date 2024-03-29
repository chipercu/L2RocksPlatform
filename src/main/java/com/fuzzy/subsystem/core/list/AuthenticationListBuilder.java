package com.fuzzy.subsystem.core.list;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.enums.AuthenticationSortingColumn;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.remote.authenticationtype.RCAuthenticationType;
import com.fuzzy.subsystem.core.textfilter.AuthenticationTextFilterGetter;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.comparators.DomainObjectStringComparator;
import com.fuzzy.subsystems.graphql.enums.SortingDirection;
import com.fuzzy.subsystems.list.ListBuilder;
import com.fuzzy.subsystems.list.ListParam;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.utils.Cache;

import java.util.Set;

public class AuthenticationListBuilder {

    private final RCExecutor<RCAuthenticationType> rcAuthenticationType;
    private final InnerListBuilder listBuilder;
    private final LanguageGetter languageGetter;
    private final Cache<String, String> typeDisplayNames;
    private Language language;

    public AuthenticationListBuilder(ResourceProvider resources) {
        rcAuthenticationType = new RCExecutor<>(resources, RCAuthenticationType.class);
        listBuilder = new InnerListBuilder(resources);
        languageGetter = new LanguageGetter(resources);
        typeDisplayNames = new Cache<>();
    }

    public ListResult<AuthenticationReadable> build(ListParam param,
                                                    Set<String> typeFilter,
                                                    AuthenticationSortingColumn sortingColumn,
                                                    SortingDirection sortingDirection,
                                                    ContextTransaction<?> context) throws PlatformException {
        typeDisplayNames.clear();
        language = languageGetter.get(context);
        listBuilder.setTypeFilter(typeFilter);
        listBuilder.setComparator(getComparator(sortingColumn, sortingDirection, context));
        return listBuilder.build(param, context);
    }

    private SorterComparator<AuthenticationReadable> getComparator(AuthenticationSortingColumn sortingColumn,
                                                                   SortingDirection sortingDirection,
                                                                   ContextTransaction<?> context) throws PlatformException {
        SorterComparator<AuthenticationReadable> comparator;
        switch (sortingColumn) {
            case NAME:
                comparator = new DomainObjectStringComparator<>(AuthenticationReadable::getName);
                break;
            case TYPE:
                comparator = new DomainObjectStringComparator<>(authentication ->
                        getTypeDisplayName(authentication.getType(), context));
                break;
            default:
                throw CoreExceptionBuilder.buildUnsupportedSortingColumnException(sortingColumn);
        }
        return comparator.direction(sortingDirection);
    }

    private String getTypeDisplayName(String type, ContextTransaction<?> context) throws PlatformException {
        if (type == null) {
            return null;
        }
        return typeDisplayNames.get(type, k ->
                rcAuthenticationType.getFirstNotNull(rc -> rc.getLocalization(k, language, context)));
    }

    private static class InnerListBuilder extends ListBuilder<AuthenticationReadable> {

        private Set<String> typeFilter;

        public InnerListBuilder(ResourceProvider resources) {
            super(
                    resources.getReadableResource(AuthenticationReadable.class),
                    new AuthenticationTextFilterGetter(resources),
                    null
            );
        }

        public void setTypeFilter(Set<String> typeFilter) {
            this.typeFilter = typeFilter;
        }

        @Override
        protected boolean checkItem(AuthenticationReadable item, ContextTransaction<?> context) throws PlatformException {
            return super.checkItem(item, context)
                    && (typeFilter == null || typeFilter.contains(item.getType()));
        }
    }
}
