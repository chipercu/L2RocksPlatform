package com.fuzzy.subsystem.ui_manager.logger;


import com.fuzzy.subsystem.ui_manager.controllers.MainController;
import javafx.application.Platform;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoginServerLogViewer extends Handler {
    @Override
    public void publish(LogRecord record) {
        Platform.runLater(() -> MainController.getLogViewers().get( MainController.LOG_VIEW_TYPE.LOGIN)
                .addLine( record.getLevel().getName() + ": " + record.getMessage()));

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
