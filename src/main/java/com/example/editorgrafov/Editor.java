package com.example.editorgrafov;

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class Editor extends Application {

    private Stage stage;
    private Pane canvas;

    private static final int defaultWindowHeight = 600;
    private static final int defaultWindowWidth = 800;

    private File file;
    private boolean changedStatus;

    private void updateStatus(File file, boolean change) {
        this.file = file;
        changedStatus = change;
        updateTitle();
    }

    private void updateTitle() {
        StringBuilder title = new StringBuilder();
        if (file == null) {
            title.append("Untitled");
        }
        else {
            title.append(file.getName());
        }
        if (changedStatus) {
            title.append("*");
        }
        title.append(" - Graph Editor");
        stage.setTitle(title.toString());
    }

    private boolean saveBeforeClosing() {
        if (changedStatus) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Graph Editor");
            alert.setHeaderText(null);

            StringBuilder alertString = new StringBuilder("Do you want to save changes to ");
            Path relativePath = FileSystems.getDefault().getPath(".");
            alertString.append(relativePath);
            alert.setContentText(alertString.toString());

            ButtonType save = new ButtonType("Save");
            ButtonType dontSave = new ButtonType("Don't save");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(save, dontSave, cancel);
            Optional<ButtonType> resultAction = alert.showAndWait();
            if (resultAction.isPresent()) {
                if (resultAction.get() == save) {
                    return saveAction();
                }
                else return true;
            }
            return false;
        }
        return false;
    }

    private FileChooser fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        //potential more extensions

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt"));
        return fileChooser;
    }

    private File chooseFileToOpen() {
        FileChooser fileChooser = fileChooser();
        fileChooser.setTitle("Open");
        return fileChooser.showOpenDialog(stage);
    }

    private File chooseFileToSave() {
        FileChooser fileChooser = fileChooser();
        fileChooser.setTitle("Save");
        return fileChooser.showSaveDialog(stage);
    }

    private boolean closeWindowRequest(WindowEvent event) {
        if (saveBeforeClosing()) {
            return true;
        }
        event.consume();
        return false;
    }

    private boolean newAction() {
        if (saveBeforeClosing()) {
            updateStatus(null, true);
            canvas.getChildren().clear();
            return true;
        }
        return false;
    }

    private boolean openAction() {
        return true;
    }

    private boolean saveAction() {
        return true;
    }

    private boolean saveAsAction() {
        return true;
    }

    private boolean exitAction() {
        if (saveBeforeClosing()) {
            Platform.exit();
            return true;
        }
        return false;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane pane = new BorderPane();

        Scene scene = new Scene(pane, defaultWindowWidth, defaultWindowHeight);

        stage.setTitle("Graph Editor");
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        this.canvas = new Pane();
        pane.getChildren().add(canvas);

        updateStatus(null, true);

        MenuBar menuBar = new MenuBar();
        pane.setTop(menuBar);

        Menu mFile = new Menu("File");
        MenuItem menuItemNew = new MenuItem("New");
        MenuItem menuItemSave = new MenuItem("Save");
        MenuItem menuItemOpen = new MenuItem("Open");
        MenuItem menuItemExport = new MenuItem("Save as");
        MenuItem menuItemExit = new MenuItem("Exit");

        mFile.getItems().addAll(menuItemNew, menuItemSave, menuItemOpen, menuItemExport, new SeparatorMenuItem(), menuItemExit);

        menuBar.getMenus().addAll(mFile);

        menuItemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuItemExport.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        menuItemExit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        menuItemNew.setOnAction(actionEvent -> newAction());
        menuItemSave.setOnAction(actionEvent -> saveAction());
        menuItemOpen.setOnAction(actionEvent -> openAction());
        menuItemExport.setOnAction(actionEvent -> saveAsAction());

        menuItemExit.setOnAction(actionEvent -> exitAction());
        stage.setOnCloseRequest(this::closeWindowRequest);

    }

    public static void main(String[] args) {
        launch(args);
    }
}