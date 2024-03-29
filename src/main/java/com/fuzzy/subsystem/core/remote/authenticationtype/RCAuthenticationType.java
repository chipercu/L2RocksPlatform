package com.fuzzy.subsystem.core.remote.authenticationtype;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;

import java.util.ArrayList;

public interface RCAuthenticationType extends QueryRemoteController {

    ArrayList<String> getTypes(ContextTransaction context) throws PlatformException;

    ArrayList<String> getAvailableForCreatingType(ContextTransaction context) throws PlatformException;

    String getLocalization(String type, Language language, ContextTransaction context) throws PlatformException;
}
