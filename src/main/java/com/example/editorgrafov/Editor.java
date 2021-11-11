package com.example.editorgrafov;

import javafx.application.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Editor extends Application {

    private static final int defaultWindowHeight = 600;
    private static final int defaultWindowWidth = 800;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane canvas = new BorderPane();

        Scene scene = new Scene(canvas, defaultWindowWidth, defaultWindowHeight);

        AtomicBoolean maxed = new AtomicBoolean(false);


        stage.setTitle("Editor grafov");
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        MenuBar menuBar = new MenuBar();
        canvas.setTop(menuBar);

        Menu mFile = new Menu("File");
        MenuItem menuItemNew = new MenuItem("New");
        MenuItem menuItemSave = new MenuItem("Save");
        MenuItem menuItemOpen = new MenuItem("Open");
        MenuItem menuItemExport = new MenuItem("Save as");

        mFile.getItems().addAll(menuItemNew, menuItemSave, menuItemOpen, menuItemExport);

        menuBar.getMenus().addAll(mFile);

        menuItemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuItemExport.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));




    }

    public static void main(String[] args) {
        launch(args);
    }
}