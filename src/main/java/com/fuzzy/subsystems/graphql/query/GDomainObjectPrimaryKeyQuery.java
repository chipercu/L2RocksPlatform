package com.fuzzy.subsystems.graphql.query;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.graphql.GDomainObject;

public class GDomainObjectPrimaryKeyQuery<K extends DomainObject, S extends GDomainObject<K>, T extends DomainObject, Y extends GDomainObject<T>>
        extends GPrimaryKeyQuery<S, T, Y> {

    private final Function<K, Long> idGetter;

    public GDomainObjectPrimaryKeyQuery(Class <T> clazz, Function<T, Y> gDomainObjectConstructor, Function<K, Long> idGetter) {
        super(clazz, gDomainObjectConstructor);
        this.idGetter = idGetter;
    }

    @Override
    protected Long getIdentificator(S source, QueryTransaction transaction) throws PlatformException {
        return idGetter.apply(source.getSource());
    }
}
