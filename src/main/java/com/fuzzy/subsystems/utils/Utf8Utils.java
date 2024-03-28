package com.fuzzy.subsystems.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Utf8Utils {

    public static boolean isUtf8(ByteBuffer in) {
        try {
            StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(in);
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    public static boolean isUtf8File(Path csvFile, int checkingLineCount) throws IOException {
        return checkFile(csvFile, checkingLineCount);
    }

    public static boolean isUtf8File(Path csvFile) throws IOException {
        return checkFile(csvFile, null);
    }

    private static boolean checkFile(Path csvFile, Integer checkingLineCount) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Files.newInputStream(csvFile, StandardOpenOption.READ), StandardCharsets.ISO_8859_1))) {
            String line;
            while ((checkingLineCount == null || --checkingLineCount >= 0) && (line = reader.readLine()) != null) {
                if (!isUtf8(ByteBuffer.wrap(line.getBytes(StandardCharsets.ISO_8859_1)))) {
                    return false;
                }
            }
            return true;
        }
    }
}
