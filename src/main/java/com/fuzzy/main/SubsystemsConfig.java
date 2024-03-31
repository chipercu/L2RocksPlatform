package com.fuzzy.main;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.utils.FileUtils;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.logback.LogDirPropertyDefiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SubsystemsConfig {

    private final static Logger log = LoggerFactory.getLogger(SubsystemsConfig.class);

    public static class Builder {

        private Path dataDir;
        private Path configDir;
        private Path dumpDataDir;
        private Path logDir;
        private Path tempDir;
        private Path workDir;
        private Path defaultDbDir;

        public Builder(Path dataDir, Path configDir, Path tempDir, Path workDir) {
            withDataDir(dataDir);
            withConfigDir(configDir);
            withTempDir(tempDir);
            withWorkDir(workDir);
        }

        public Builder withDataDir(Path dataDir) {
            this.dataDir = dataDir.normalize();
            return this;
        }

        public Builder withConfigDir(Path configDir) {
            this.configDir = configDir.normalize();
            return this;
        }

        public Builder withTempDir(Path tempDir) {
            this.tempDir = tempDir.normalize();
            return this;
        }

        public Builder withWorkDir(Path workDir) {
            this.workDir = workDir.normalize();
            return this;
        }

        public Path getDataDir() {
            return dataDir;
        }

        public Path getWorkDir() {
            return workDir;
        }

        public Path getConfigDir() {
            return configDir;
        }

        public SubsystemsConfig build() throws ConfigBuilderException {
            this.dumpDataDir = dataDir.resolve("dump");
            this.logDir = Paths.get(LogDirPropertyDefiner.LOG_DIR);
            final Path databases = dataDir.resolve("databases");
            this.defaultDbDir = databases.resolve("main");
            try {
                FileUtils.ensureDirectory(this.dataDir);
                FileUtils.ensureDirectory(this.workDir);
                FileUtils.ensureDirectory(this.configDir);
                FileUtils.ensureDirectory(this.dumpDataDir);
                FileUtils.ensureDirectory(this.logDir);
                FileUtils.ensureDirectory(this.tempDir);
                FileUtils.ensureDirectory(databases);
                FileUtils.ensureDirectory(this.defaultDbDir);
            } catch (PlatformException e) {
                throw new ConfigBuilderException(e);
            }
            return new SubsystemsConfig(this);
        }

        public static Builder withDefaultPath(Path dataDir, Path workDir) {
            return new Builder(
                    dataDir, dataDir.resolve("config"), dataDir.resolve("temp"),
                    workDir
            );
        }
    }

    private final Path dataDir;
    private final Path workDir;
    private final Path configDir;
    private final Path dumpDataDir;
    private final Path logDir;
    private final Path tempDir;
    private Path defaultDbDir;

    private SubsystemsConfig(Builder builder) {
        this.dataDir = builder.dataDir;
        this.workDir = builder.workDir;
        this.configDir = builder.configDir;
        this.dumpDataDir = builder.dumpDataDir;
        this.logDir = builder.logDir;
        this.tempDir = builder.tempDir;
        this.defaultDbDir = builder.defaultDbDir;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getWorkDir() {
        return workDir;
    }

    public Path getDumpDataDir() {
        return dumpDataDir;
    }

    public Path getLogDir() {
        return logDir;
    }

    public Path getTempDir() {
        return tempDir;
    }

    public Path getDefaultDbDir() {
        return defaultDbDir;
    }
}
