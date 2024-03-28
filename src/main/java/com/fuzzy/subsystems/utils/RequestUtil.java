package com.fuzzy.subsystems.utils;

import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.struct.ClusterFile;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class RequestUtil {

    public static Path uploadFile(GRequestHttp request, Subsystem subsystem) throws PlatformException {
        ArrayList<GRequestHttp.UploadFile> uploadFiles = request.getUploadFiles();
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            throw GeneralExceptionBuilder.buildFileNotExistsException();
        } else if (uploadFiles.size() > 1) {
            throw GeneralExceptionBuilder.buildLoadManyFileInRequestException();
        }
        GRequestHttp.UploadFile uploadFile = uploadFiles.get(0);
        ClusterFile clusterFile = new ClusterFile(subsystem, uploadFile.uri);
        Path filePath;
        if (clusterFile.isLocalFile()) {
            filePath = Paths.get(clusterFile.getUri());
        } else {
            filePath = TempFiles.buildTempFilePath();
            clusterFile.copyTo(filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return filePath;
    }

    public static byte[] getFileByteArray(GRequestHttp request, Subsystem subsystem) throws PlatformException {
        ArrayList<GRequestHttp.UploadFile> uploadFiles = request.getUploadFiles();
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return null;
        } else if (uploadFiles.size() > 1) {
            throw GeneralExceptionBuilder.buildLoadManyFileInRequestException();
        }
        byte[] result;
        try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
            new ClusterFile(subsystem, uploadFiles.get(0).uri).copyTo(byteArray);
            result = byteArray.toByteArray();
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
        return result;
    }
}