package com.fuzzy.subsystem.ui_manager.controllers;


import com.fuzzy.subsystem.gameserver.GameStart;
import com.fuzzy.subsystem.loginserver.L2LoginServer;
import com.fuzzy.subsystem.loginserver.L2LoginStart;
import com.fuzzy.subsystem.loginserver.gameservercon.GSConnection;
import com.fuzzy.subsystem.ui_manager.UI_Manager;
import com.fuzzy.subsystem.ui_manager.ui_models.LogViewer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    private double xOffset, yOffset;
    private static final Map<LOG_VIEW_TYPE, LogViewer> logViewerMap = new HashMap<>();

    public enum LOG_VIEW_TYPE{
        LOGIN, GAME
    }

    @FXML
    private Button gameStartButton;

    @FXML
    private Button loginStartButton;

    @FXML
    private AnchorPane MainPane;

    @FXML
    private Button CLOSE_BUTTON;

    @FXML
    private Button COLAPSE_BUTTON;

    @FXML
    private AnchorPane TOP_BAR;

    @FXML
    private Button showGameLogButton;

    @FXML
    private Button showLoginLogButton;

    @FXML
    protected void onClickShowLoginLog(){
        LogViewer logViewer = logViewerMap.get(LOG_VIEW_TYPE.LOGIN);
        if (logViewer.isOpen()){
            logViewer.hide();
            logViewerMap.forEach((logViewType, viewer) -> {
                if (viewer.isOpen()){
                    setLocFromLogView(viewer);
                }
            });
        }else {
            openLogViewer(logViewer);
        }

    }


    @FXML
    protected void onClickShowGameLog(){
        LogViewer gameViewer = logViewerMap.get(LOG_VIEW_TYPE.GAME);
        if (gameViewer.isOpen()){
            gameViewer.hide();
            logViewerMap.forEach((logViewType, viewer) -> {
                if (viewer.isOpen()){
                    setLocFromLogView(viewer);
                }

            });
        }else {
            openLogViewer(gameViewer);
        }

    }

    private void setLocFromLogView(LogViewer logViewer){
        if (isSpaceOccupied(logViewer)){
            logViewer.setX(logViewer.getX() + logViewer.getWidth() + 2);
            setLocFromLogView(logViewer);
        }
    }

    private void openLogViewer(LogViewer logViewer){
        double x = UI_Manager.getStage().getX() + UI_Manager.getStage().getWidth() + 2;
        double y = UI_Manager.getStage().getY();
        logViewer.setX(x);
        logViewer.setY(y);
        setLocFromLogView(logViewer);
        logViewer.open();
    }


    public static Map<LOG_VIEW_TYPE, LogViewer> getLogViewers() {
        return logViewerMap;
    }

    @FXML
    protected void startApp1(){
        final Thread thread = new Thread(() -> L2LoginStart.main(new String[]{}));
        thread.start();
        thread.interrupt();
    }
    @FXML
    protected void startApp2() {
        new Thread(() -> {
            try {
                GameStart.main(new String[]{});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    void initialize(){
        logViewerMap.put(LOG_VIEW_TYPE.LOGIN, new LogViewer(MainPane, "Login logs"));
        logViewerMap.put(LOG_VIEW_TYPE.GAME, new LogViewer(MainPane, "Game logs"));
        setDragged(TOP_BAR);

        loginStartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (L2LoginServer.getInstance() == null){
                new Thread(() -> L2LoginStart.main(new String[]{})).start();
                loginStartButton.setText("Остановить Login Server");
            }else if (L2LoginServer.getInstance().getGameServerListener().isShutdown()){
                loginStartButton.setText("Остановить Login Server");
            } else {
                loginStartButton.setText("Запуск Login Server");
            }
        });

        CLOSE_BUTTON.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            final Stage stage = (Stage) CLOSE_BUTTON.getScene().getWindow();
            logViewerMap.forEach((logViewType, viewer) -> viewer.close());
            System.exit(0);
            stage.close();
        });

        COLAPSE_BUTTON.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            final Stage stage = (Stage) COLAPSE_BUTTON.getScene().getWindow();
            stage.setIconified(true);
        });




    }

    private boolean isSpaceOccupied(LogViewer logViewer){
        List<LogViewer> collect = new ArrayList<>(logViewerMap.values());
        for (LogViewer viewer: collect){
            if (viewer == logViewer){
                continue;
            }
            if (!viewer.isOpen()){
                continue;
            }

            if (viewer.isIntersected(logViewer)){
                return true;
            }
        }
        return false;
    }


    private void setDragged(Node... nodes) {
        for (Node node : nodes) {
            node.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            node.setOnMouseDragged(event -> {
                node.getScene().getWindow().setX(event.getScreenX() - xOffset);
                node.getScene().getWindow().setY(event.getScreenY() - yOffset);
            });
        }
    }


}
