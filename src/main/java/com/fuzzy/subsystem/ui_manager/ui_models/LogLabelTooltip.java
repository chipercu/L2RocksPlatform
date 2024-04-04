package com.fuzzy.subsystem.ui_manager.ui_models;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by a.kiperku
 * Date: 02.04.2024
 */

public class LogLabelTooltip extends Stage {
    private Scene scene;
    private final Label label;

    public LogLabelTooltip(String text) {
        this.label = new Label(text);
        this.label.autosize();
    }

    public void show(double x, double y){
        this.setX(x);
        this.setY(y);
        this.scene = new Scene(label, label.getWidth(), label.getHeight());
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(scene);
        super.show();
    }

}
