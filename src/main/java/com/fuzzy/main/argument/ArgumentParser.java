package com.fuzzy.main.argument;

import com.fuzzy.main.argument.upgrade.ArgumentUpgrade;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ArgumentParser {

    private static final String OPTION_DATA_DIR = "data_dir";
    private static final String OPTION_CONFIG_DIR = "config_dir";
    private static final String OPTION_TEMP_DIR = "temp_dir";

    private static final String OPTION_WORK_DIR = "work_dir";

    private static final String OPTION_UPGRADE = "upgrade";
    private static final String OPTION_AUTO_UPGRADE = "auto_upgrade";

    public final Path dataDirPath;
    public final Path configDirPath;
    public final Path tempDirPath;
    public final Path workDirPath;

    public final ArgumentUpgrade argumentUpdate;
    public final boolean autoUpgrade;

    public ArgumentParser(String[] args) throws InterruptedException {
        Options options = new Options()
                .addOption(Option.builder()
                        .longOpt(OPTION_DATA_DIR)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Absolute path to data directory")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_CONFIG_DIR)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Absolute path to config directory")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_TEMP_DIR)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Absolute path to temp directory")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_WORK_DIR)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Absolute path to working directory")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_UPGRADE)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Upgrade modules")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_AUTO_UPGRADE)
                        .hasArg(true)
                        .optionalArg(true)
                        .desc("Auto upgrade modules")
                        .build());

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);

            dataDirPath = Paths.get(cmd.getOptionValue(OPTION_DATA_DIR, Paths.get("data").toAbsolutePath().toString())).normalize();
            configDirPath = Paths.get(cmd.getOptionValue(OPTION_CONFIG_DIR, dataDirPath.resolve("config").toAbsolutePath().toString())).normalize();
            tempDirPath = Paths.get(cmd.getOptionValue(OPTION_TEMP_DIR, dataDirPath.resolve("temp").toAbsolutePath().toString())).normalize();

            workDirPath = Paths.get(cmd.getOptionValue(OPTION_WORK_DIR, Paths.get(".").toAbsolutePath().toString())).normalize();
            if (!dataDirPath.isAbsolute() || !workDirPath.isAbsolute()) {
                throw new IllegalArgumentException("Directory path must be absolutely.");
            }

            if (cmd.hasOption(OPTION_UPGRADE)) {
                String upgrade = cmd.getOptionValue(OPTION_UPGRADE);
                argumentUpdate = ArgumentUpgrade.build(upgrade);
            } else {
                argumentUpdate = null;
            }

            if (cmd.hasOption(OPTION_AUTO_UPGRADE)) {
                autoUpgrade = Boolean.valueOf(cmd.getOptionValue(OPTION_AUTO_UPGRADE, "false"));
            } else {
                autoUpgrade = false;
            }

        } catch (ParseException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            new HelpFormatter().printHelp("app_name", options);

            throw new InterruptedException();
        }
    }
}
