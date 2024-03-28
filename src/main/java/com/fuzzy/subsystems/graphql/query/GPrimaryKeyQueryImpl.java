package com.fuzzy.subsystems.graphql.query;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.graphql.GDomainObject;

public class GPrimaryKeyQueryImpl<S extends RemoteObject, T extends DomainObject, Y extends GDomainObject<T>>
        extends GPrimaryKeyQuery<S, T, Y> {

    private Long primaryKey;

    public GPrimaryKeyQueryImpl(Class <T> clazz, Function<T, Y> gDomainObjectConstructor, Long primaryKey) {
        super(clazz, gDomainObjectConstructor);
        this.primaryKey = primaryKey;
    }

    @Override
    protected Long getIdentificator(S source, QueryTransaction transaction) throws PlatformException {
        return primaryKey;
    }
}
