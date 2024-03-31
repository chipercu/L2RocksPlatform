package com.fuzzy.subsystems;

import com.fuzzy.main.Subsystems;
import com.fuzzy.main.SubsystemsConfig;
import com.fuzzy.main.argument.ArgumentParser;
import com.fuzzy.main.argument.upgrade.ArgumentUpgrade;
import com.fuzzy.platform.sdk.context.impl.ContextImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.platform.update.core.ModuleUpdateEntity;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.FutureTask;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ArgumentParser argumentParser;
        try {
            argumentParser = new ArgumentParser(args);
        } catch (InterruptedException e) {
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.System.TYPE_CRUSH),
                    new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                    new ContextImpl(new SourceSystemImpl())
            );
            System.exit(2);
            return;
        }
        SubsystemsConfig config = new SubsystemsConfig.Builder(
                argumentParser.dataDirPath,
                argumentParser.configDirPath,
                argumentParser.tempDirPath,
                argumentParser.workDirPath
        ).build();
        executeAsApp(new Subsystems.Builder(config, (thread, throwable) -> crash(throwable)), argumentParser);
    }

    public static void executeAsApp(Subsystems.Builder builder, ArgumentParser argumentParser) {
        Subsystems subsystems;
        try {
            subsystems = builder.build();
        } catch (Throwable e) {
            crash(e);
            return;
        }
        try {
            if (isNeedToInstall(builder.config)) {
                subsystems.install();
            } else if (argumentParser.argumentUpdate != null) {
                subsystems.update(getRequiredUpdates(argumentParser.argumentUpdate));
            } else if (argumentParser.autoUpgrade) {
                subsystems.upgrade();
            }
            subsystems.start();

            FutureTask<Void> stopSignal = new FutureTask<>(() -> null);
            Runtime.getRuntime().addShutdownHook(new Thread(stopSignal, "shutDownHook"));
            stopSignal.get();
            log.debug("App shutDownHook");
            subsystems.stop();
        } catch (Throwable e) {
            crash(e);
        }
        subsystems.close();
    }

    public static void crash(Throwable e) {
        try {
            log.error("Application crashing ", e);
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.System.TYPE_CRUSH),
                    new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                    new ContextImpl(new SourceSystemImpl())
            );
        } catch (Throwable thr) {
            log.error("Exception", thr);
        } finally {
            System.exit(1);
        }
    }

    private static boolean isNeedToInstall(SubsystemsConfig config) throws IOException {
        //Завязал на существовании папки БД
        Path databaseDirPath = config.getDefaultDbDir();
        return Files.notExists(databaseDirPath) || Files.list(databaseDirPath).count() == 0;
    }

    private static ModuleUpdateEntity[] getRequiredUpdates(ArgumentUpgrade argumentUpdate) {
        return argumentUpdate.updateModules.stream()
                .map(updateModule -> new ModuleUpdateEntity(updateModule.oldVersion, updateModule.newVersion, updateModule.uuid))
                .toArray(ModuleUpdateEntity[]::new);
    }
}
