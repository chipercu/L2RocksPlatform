package com.fuzzy.subsystem.ui_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class UI_Manager extends Application {

    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        URL resource = UI_Manager.class.getResource("fxml/UI_Main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Launcher");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        UI_Manager.stage = stage;
    }

    public static Stage getStage(){
        return stage;
    }


    public void launchManager(){
        launch();
    }




}
