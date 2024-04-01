package com.fuzzy.subsystem.extensions.scripts;

import java.util.logging.Logger;

/**
 * <p>
 * Просто интерфейс с методами которые обязательно должны использоваться в скриптах.
 */
public interface ScriptFile {
    Logger _log = Logger.getLogger(ScriptFile.class.getName());

    /**
     * Вызывается при загрузке классов скриптов
     */
    void onLoad();

    /**
     * Вызывается при перезагрузке
     * После перезагрузки onLoad() вызывается автоматически
     */
    void onReload();

    /**
     * Вызывается при выключении сервера
     */
    void onShutdown();
}
