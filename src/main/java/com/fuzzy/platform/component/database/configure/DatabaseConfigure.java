package com.fuzzy.platform.component.database.configure;

import com.fuzzy.platform.component.database.DatabaseComponentExtension;

import java.nio.file.Path;

public class DatabaseConfigure {

    public final Path dbPath;
    public final DatabaseComponentExtension extension;

    private DatabaseConfigure(Builder builder) {
        this.dbPath = builder.dbPath;
        this.extension = builder.extension;
    }

    public static class Builder {

        private Path dbPath;
        private DatabaseComponentExtension extension;

        public Builder withPath(Path dbPath) {
            this.dbPath = dbPath;
            return this;
        }

        public Builder withExtension(DatabaseComponentExtension extension) {
            this.extension = extension;
            return this;
        }

        public DatabaseConfigure build() {
            return new DatabaseConfigure(this);
        }
    }
}
