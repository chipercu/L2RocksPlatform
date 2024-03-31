package com.fuzzy.subsystems.dump;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.main.SubsystemsConfig;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystems.logback.LogNamePropertyDefiner;
import com.fuzzy.subsystems.remote.dump.RControllerEventDump;
import net.minidev.json.JSONObject;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class Dump {

    private static final Logger log = LoggerFactory.getLogger(Dump.class);

    private static final String APP_NAME = "L2RocksPlatform";
    private static final String APP_TYPE = "server";

    public synchronized static void build(SubsystemsConfig subsystemsConfig, Cluster cluster) {
        try {
            long now = System.currentTimeMillis();

            Path pathDumpFile = subsystemsConfig.getDumpDataDir().resolve(APP_NAME + "_dump_" +
                    new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date(now)) +
                    ".tar.gz"
            );

            try (OutputStream outBackup = Files.newOutputStream(pathDumpFile, StandardOpenOption.CREATE_NEW);
                 BufferedOutputStream outBufferedBackup = new BufferedOutputStream(outBackup);
                 GZIPOutputStream outGZIPBufferedBackup = new GZIPOutputStream(outBufferedBackup);
                 TarArchiveOutputStream taos = new TarArchiveOutputStream(outGZIPBufferedBackup)) {
                taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
                taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

                try {
                    packInfo(buildInfo(cluster), taos);
                } catch (Throwable e) {
                    log.error("Build info.json failed", e);
                }

                try {
                    Path fileDumpHeap = subsystemsConfig.getDumpDataDir().resolve("dump_heap_" + now + ".hprof");
//                    HeapDumpBuilder.dumpHeap(fileDumpHeap);
                    packHeap(fileDumpHeap, taos);
//                    FileUtils.deleteQuietly(fileDumpHeap.toFile());
                } catch (Throwable e) {
                    log.error("Build heap dump failed", e);
                }

                Path fileLog = subsystemsConfig.getLogDir().resolve(LogNamePropertyDefiner.FILENAME);
                if (Files.exists(fileLog)) {
                    packLog(fileLog, taos);
                }
            }

            //Оповещаем подписчиков о новом дампе
            if (cluster == null) {
                log.warn("Fail event handler dump");
            } else {
                CoreSubsystem coreSubsystem = cluster.getAnyLocalComponent(CoreSubsystem.class);
                if (coreSubsystem == null) {
                    log.warn("Fail event handler dump");
                } else {
                    for (RControllerEventDump controllerEventDump : coreSubsystem.getRemotes().getControllers(RControllerEventDump.class)) {
                        controllerEventDump.action(pathDumpFile.toUri());
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Build dump failed", e);
        }
    }

    private static void packInfo(byte[] source, TarArchiveOutputStream destination) throws IOException {
        TarArchiveEntry taeInfo = new TarArchiveEntry("info.json");
        taeInfo.setSize(source.length);
        destination.putArchiveEntry(taeInfo);
        destination.write(source);
        destination.flush();
        destination.closeArchiveEntry();
    }

    private static void packHeap(Path source, TarArchiveOutputStream destination) throws IOException {
        TarArchiveEntry taeHeapDump = new TarArchiveEntry(source.toFile(), "heap.hprof");
        destination.putArchiveEntry(taeHeapDump);
        try (InputStream fis = Files.newInputStream(source)) {
            IOUtils.copy(fis, destination);
        }
        destination.flush();
        destination.closeArchiveEntry();
    }

    private static void packLog(Path source, TarArchiveOutputStream destination) throws IOException {
        TarArchiveEntry taeFileLog = new TarArchiveEntry(source.toFile(), LogNamePropertyDefiner.FILENAME);
        destination.putArchiveEntry(taeFileLog);
        try (InputStream fis = Files.newInputStream(source)) {
            IOUtils.copy(fis, destination);
        }
        destination.flush();
        destination.closeArchiveEntry();
    }

    private static byte[] buildInfo(Cluster cluster) {
        JSONObject out = new JSONObject();
        out.put("app", APP_NAME);
        out.put("type", APP_TYPE);
        out.put("platform", SystemUtils.OS_NAME);
        return out.toJSONString().getBytes(StandardCharsets.UTF_8);
    }
}
