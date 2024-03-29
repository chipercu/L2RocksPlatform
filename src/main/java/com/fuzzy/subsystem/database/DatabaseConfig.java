package com.fuzzy.subsystem.database;

import com.infomaximum.main.SubsystemsConfig;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.subsystem.SubsystemConfig;
import net.minidev.json.JSONObject;

import java.nio.file.Path;

public class DatabaseConfig extends SubsystemConfig {

    private static final String DEFAULT_BACKUP_DIR_NAME = "backup";

    private static final String JSON_BACKUP_PATH = "backup_path";
    private static final String JSON_PERIODICAL_BACKUP_ENABLED = "periodical_backup_enabled";

    private final Path dbPath;
    private final Path backupDir;
    private final boolean periodicalBackupEnabled;

    private DatabaseConfig(Builder builder) {
        super(builder);
        this.dbPath = builder.dbPath;
        this.backupDir = builder.backupDir;
        this.periodicalBackupEnabled = builder.periodicalBackupEnabled;
    }

    public Path getDbPath() {
        return dbPath;
    }

    public Path getBackupDir() {
        return backupDir;
    }

    public boolean isPeriodicalBackupEnabled() {
        return periodicalBackupEnabled;
    }

    public static class Builder extends SubsystemConfig.Builder {

        private Path dbPath = null;
        private Path backupDir = null;
        private boolean periodicalBackupEnabled = true;

        public Builder(String componentUuid, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            super(componentUuid, subsystemsConfig);
            init();
            save();
        }

        public Builder withDbPath(Path value) {
            this.dbPath = value;
            return this;
        }

        public Builder withBackupDir(Path value) {
            this.backupDir = value;
            return this;
        }

        public Builder withPeriodicalBackupEnabled(boolean value) {
            this.periodicalBackupEnabled = value;
            return this;
        }

        @Override
        public DatabaseConfig build(){
            return new DatabaseConfig(this);
        }

        private void init() throws ConfigBuilderException {
            JSONObject json = readJSON();
            dbPath = getSubsystemsConfig().getDefaultDbDir();
            backupDir = getPath(JSON_BACKUP_PATH, json, DEFAULT_BACKUP_DIR_NAME, getSubsystemsConfig().getDataDir());
            if (json.containsKey(JSON_PERIODICAL_BACKUP_ENABLED)) {
                periodicalBackupEnabled = (boolean)json.get(JSON_PERIODICAL_BACKUP_ENABLED);
            }
        }

        private void save() throws ConfigBuilderException {
            JSONObject json = new JSONObject();
            json.put(JSON_BACKUP_PATH, tryRelativizePath(backupDir, getSubsystemsConfig().getDataDir()).toString());
            json.put(JSON_PERIODICAL_BACKUP_ENABLED, periodicalBackupEnabled);
            saveJSON(json);
        }
    }
}
