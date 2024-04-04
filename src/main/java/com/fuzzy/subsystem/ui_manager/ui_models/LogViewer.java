package com.fuzzy.subsystem.ui_manager.ui_models;

import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LogViewer extends Stage {

    private final String title;
    private final AnchorPane parentPane;
    private final Button hideButton;
    private final Button clearButton;
    private final Button testButton;
    private Scene scene;
    private final AnchorPane anchorPane;
    private ListView<Label> logsListView;

    private double xOffset, yOffset;


    private boolean isOpen = false;


    public LogViewer(AnchorPane parentPane, String title) {
        this.title = title;
        this.initStyle(StageStyle.UNDECORATED);
        this.parentPane = parentPane;
        this.logsListView = initListView();
        this.hideButton = initHideButton();
        this.clearButton = initClearButton();
        this.testButton = initTestButton();
        this.anchorPane = initAnchorPane();
        setDragged(anchorPane);
    }

    private AnchorPane initAnchorPane() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(250, 600);
        Label titleLabel = new Label(title);
        titleLabel.setPrefSize(100, 25);
        titleLabel.setLayoutX(10);
        titleLabel.setLayoutY(2);

        anchorPane.getChildren().addAll(hideButton, clearButton, logsListView, testButton, titleLabel);
        return anchorPane;
    }

    private ListView<Label> initListView() {
        ListView<Label> objectListView = new ListView<>();
        objectListView.setPrefSize(240, 500);
        objectListView.setLayoutX(5);
        objectListView.setLayoutY(50);
        return objectListView;
    }

    private Button initHideButton() {
        Button button = new Button("X");
        button.setPrefSize(25, 25);
        button.setLayoutX(225);
        button.setLayoutY(0);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> this.hide());
        return button;
    }

    private Button initClearButton() {
        Button button = new Button("Очистить");
        button.setPrefSize(75, 25);
        button.setLayoutX(150);
        button.setLayoutY(550);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> logsListView.getItems().clear());
        return button;
    }

    private Button initTestButton() {
        Button button = new Button("Test");
        button.setPrefSize(75, 25);
        button.setLayoutX(50);
        button.setLayoutY(550);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addLine("WARNING: Loading FXML document with JavaFX API of version 21 by JavaFX runtime of version 8.0.202-ea" + Math.random() * 100));
        return button;
    }

    public void addLine(String text) {
        Label label = new Label();
        label.setText(text);
        label.setTextFill(Color.GREEN);

        Tooltip tooltip = new Tooltip();
        tooltip.setMaxWidth(300);
        tooltip.setText(text);
        tooltip.setWrapText(true);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(label, tooltip);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            if (event.getButton() == MouseButton.SECONDARY){
                ContextMenu contextMenu = new ContextMenu();
                MenuItem copy = new MenuItem("Копировать");

                copy.setOnAction(copyEvent -> {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(label.getText());
                    clipboard.setContent(content);
                });
                MenuItem delete = new MenuItem("Удалить");
                delete.setOnAction(deleteEvent -> logsListView.getItems().remove(label));
                contextMenu.getItems().addAll(copy, delete);
                contextMenu.show(label, event.getScreenX(), event.getScreenY());
            }
        });

        this.logsListView.getItems().add(label);
    }

    public void open() {
        if (scene == null) {
            scene = new Scene(anchorPane, 250, 600);
            this.setScene(scene);
        }
        this.show();
    }

    public BoundingBox getBound() {
        return new BoundingBox(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean isIntersected(LogViewer viewer) {
        return this.getBound().intersects(viewer.getBound());
    }


    public boolean isOpen() {
        return this.isShowing();
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
