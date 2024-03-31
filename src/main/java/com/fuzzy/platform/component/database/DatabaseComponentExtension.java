package com.fuzzy.platform.component.database;

public interface DatabaseComponentExtension {

    void initialize(com.fuzzy.platform.component.database.DatabaseComponent databaseComponent);

    void onStart(DatabaseComponent databaseComponent);
}
