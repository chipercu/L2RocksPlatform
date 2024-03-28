package com.fuzzy.subsystems.utils;

import com.fuzzy.subsystems.exception.runtime.UnimplementedPlatformException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ProcessInfoUtils {

    private final static Logger log = LoggerFactory.getLogger(ProcessInfoUtils.class);

    private static String fqdn = null;
    private static String pid = null;

    static {
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName != null && processName.length() > 0) {
                String[] parse = processName.split("@");
                fqdn = parse[1];
                pid = parse[0];
            }
        } catch (Throwable e) {
            log.error("Error log initialization process info", e);
        }
    }

    public static String getFQDN() {
        return fqdn;
    }

    public static String getPID() {
        return pid;
    }

    public static void waitForProcess(Process process, Charset consoleCharset) throws IOException {
        try {
            int errorLevel = process.waitFor();
            if (errorLevel != 0) {
                String consoleText = "";
                if (consoleCharset != null) {
                    try {
                        consoleText = "\r\n" +
                                ProcessInfoUtils.processStreamToString(process.getInputStream(), consoleCharset).trim();
                    } catch (Throwable ignored) {
                    }
                }
                throw new IOException("Process has finished with error code=" + errorLevel + consoleText);
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static Charset getWindowsConsoleCharset() throws IOException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new UnimplementedPlatformException("getWindowsConsoleCharset");
        }
        String cmd = System.getenv("windir") + "\\system32\\chcp.com";
        Process process = Runtime.getRuntime().exec(cmd);
        waitForProcess(process, null);
        try (Scanner scanner = new Scanner(
                new InputStreamReader(process.getInputStream()))
        ) {
            String consoleCodePage = scanner.skip(".*:").next();
            String[] charsetPrefixes =
                    new String[]{ "", "windows-", "x-windows-", "IBM", "x-IBM" };
            for (String charsetPrefix : charsetPrefixes) {
                try {
                    return Charset.forName(charsetPrefix + consoleCodePage);
                } catch (Throwable ignored) {
                }
            }
            return Charset.defaultCharset();
        }
    }

    /**
     * Переводит stdout или stderr в текст.
     * Закрывает переданный поток.
     */
    public static String processStreamToString(InputStream stream, Charset consoleCharset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, consoleCharset))) {
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line).append("\r\n");
            }
            return message.toString();
        }
    }

    /**
     * @return Если ПРИОСТАНОВИТЬ службу "Инструментарий управления Windows (WinMgmt)", утилита зависнет на полминуты и выдаст ошибку.
     * Метод вернет null
     */
    public static Path getProcessPath(String pid, Charset consoleCharset) throws IOException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new UnimplementedPlatformException("getProcessPath");
        }
        final String PATH_FIELD = "ExecutablePath=";
        Process process = Runtime.getRuntime().exec(
                "wmic process where \"ProcessID=" + pid + "\" get " + PATH_FIELD.substring(0, PATH_FIELD.length() - 1) + " /FORMAT:LIST");
        waitForProcess(process, consoleCharset);
        String output = processStreamToString(process.getInputStream(), consoleCharset).trim();
        if (!PATH_FIELD.regionMatches(true, 0, output, 0, PATH_FIELD.length())) {
            throw new IOException("Cannot obtain executable path from wmic result: \r\n" + output);
        }
        return Paths.get(output.substring(PATH_FIELD.length()));
    }
}
