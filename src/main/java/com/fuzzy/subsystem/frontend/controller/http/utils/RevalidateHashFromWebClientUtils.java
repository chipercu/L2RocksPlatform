package com.fuzzy.subsystem.frontend.controller.http.utils;

import com.fuzzy.main.Subsystems;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by kris on 16.02.17.
 */
public class RevalidateHashFromWebClientUtils {

    //Тут не применяем volatile и synchronized, так как для нас не критичен результат гонки потоков
    private static String _revalidateHash = null;

    //WARN: Ulitin V. В будущем необходимо добавить изменение хеша в зависимости от подключенных подсистем и их версий
    public static String getRevalidateHash() throws IOException {
        if (_revalidateHash == null) {
            FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

            Path pathIndexHtml = frontEndSubSystem.getConfig().getWebPath().resolve("index.html");

            BasicFileAttributes attrsIndexHtml = Files.readAttributes(pathIndexHtml, BasicFileAttributes.class);
            long timeLastModified = attrsIndexHtml.lastModifiedTime().toMillis();

            String srcRevalidateHash = new StringBuilder()
                    .append(timeLastModified)
                    .append(Subsystems.VERSION.toString())
                    .toString();

            //Применяется алгоритм sha1, так как для нас критична не безопасность, а скорость
            _revalidateHash = DigestUtils.sha1Hex(srcRevalidateHash);
        }
        return _revalidateHash;
    }
}
