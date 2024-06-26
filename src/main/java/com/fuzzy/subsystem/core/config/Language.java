package com.fuzzy.subsystem.core.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.database.utils.BaseEnum;

import java.util.Locale;

/**
 * Created by kris on 17.03.17.
 */
@GraphQLTypeOutObject("language")
public enum Language implements RemoteObject, BaseEnum {

    ENGLISH(1, Locale.ENGLISH),

    RUSSIAN(2, new Locale("ru"));

    private final int id;
    private final Locale locale;

    Language(int id, Locale locale) {
        this.id = id;
        this.locale = locale;
    }

    @Override
    public int intValue() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Language get(long id) {
        for (Language type : Language.values()) {
            if (type.intValue() == id) {
                return type;
            }
        }
        return null;
    }

}