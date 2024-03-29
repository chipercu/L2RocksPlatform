package com.fuzzy.subsystem.core.remote.depersonalization;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectSource;
import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.rdao.database.domainobject.filter.IdFilter;
import com.fuzzy.main.rdao.database.domainobject.iterator.IteratorEntity;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.subsystems.config.Config;
import com.fuzzy.subsystems.domainobject.config.ConfigEditable;
import com.fuzzy.subsystems.domainobject.config.ConfigReadable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DepersonalizationUtils {

    private static final int COMMIT_ROW_COUNT = 1000;

    public static <T extends DomainObject & DomainObjectEditable> void depersonalizeDomain(
            DomainObjectSource domainObjectSource,
            Class<T> tClass,
            Consumer<T> consumer
    ) throws DatabaseException {
        long id = 0;
        boolean next = true;
        while (next && id != Long.MAX_VALUE){
            try (Transaction transaction = domainObjectSource.buildTransaction();
                 IteratorEntity<T> ie = transaction.find(tClass, new IdFilter(id + 1))) {
                int counter = 0;
                while (next = ie.hasNext()) {
                    T object = ie.next();
                    id = object.getId();
                    consumer.accept(object);
                    transaction.save(object);
                    counter++;
                    if (counter == COMMIT_ROW_COUNT) {
                        break;
                    }
                }
                transaction.commit();
            }
        }
    }

    public static void depersonalizeField(Supplier<String> getter, Consumer<String> setter, Long postfix) {
        String value = getter.get();
        if (!StringUtils.isEmpty(value)) {
            String newValue = DigestUtils.sha256Hex(value);
            if (postfix != null) {
                newValue += postfix;
            }
            setter.accept(newValue);
        }
    }

    public static <T extends ConfigReadable & ConfigEditable> void removeConfigs(
            DomainObjectSource domainObjectSource,
            Class<T> tClass,
            Config<?>... configs
    ) throws DatabaseException {
        try (Transaction transaction = domainObjectSource.buildTransaction()) {
            for (Config<?> config : configs) {
                HashFilter filter = new HashFilter(T.FIELD_NAME, config.getName());
                try (IteratorEntity<T> ie = transaction.find(tClass, filter)) {
                    while (ie.hasNext()) {
                        transaction.remove(ie.next());
                    }
                }
            }
            transaction.commit();
        }
    }
}
