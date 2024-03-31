package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.platform.exception.PlatformException;

import java.io.Serializable;

public interface IConfig extends Serializable {
    void check() throws PlatformException;
}
