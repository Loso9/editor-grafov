package com.example.editorgrafov;

import com.example.editorgrafov.enums.Action;
import com.example.editorgrafov.enums.Mode;
import javafx.application.*;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.scene.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Editor extends Application {

    private Stage stage;
    private Pane canvas;
    private MenuItem menuItemSave;
    private RadioMenuItem menuItemInsert;
    private File file;
    private Graph graph;
    private boolean changedStatus;
    private String manualContent;
    private TextArea manualTextArea;
    private ToggleGroup modes;
    private boolean manualOpened;
    private List<Pair<String, TextField>> listOfTextFields;
    private List<Triplet<String, Button, Mode>> listOfButtons;
    private List<Pair<String, Mode>> listOfModes;
    private Map<String, Stack<Vertex>> vertices;

    private static final int defaultWindowHeight = 800;
    private static final int defaultWindowWidth = 1200;

    private static final int canvasHeight = defaultWindowHeight;
    private static final int canvasWidth = defaultWindowWidth - 200;


    /**
     * Updates current session status
     * @param file or null, if session is not currently saved in any file
     * @param change in content of current session
     */
    public void updateStatus(File file, boolean change) {
        this.file = file;
        changedStatus = change;
        updateTitle();
    }

    /**
     * Updates title
     * - if not saved yet - Untitled
     * - else file name
     * -- if user change content of current session, "*" is added in front of file name
     * + (- Graph editor)
     */
    public void updateTitle() {
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

    /**
     * Method for displaying "Save before closing" alert in case the current session was not saved
     * @return boolean value, whether alert was handled correctly or if it was needed at all
     */
    public boolean saveBeforeClosing() {
        if (changedStatus) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Graph Editor");
            alert.setHeaderText(null);
            StringBuilder alertString = new StringBuilder();
            if (file == null) alertString.append("Do you want to save before closing?");
            else {
                alertString = new StringBuilder("Do you want to save changes to ");
                String path;
                path = file.getAbsolutePath();
                alertString.append(path);
            }
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
                return resultAction.get() == dontSave;
            }
            return false;
        }
        return true;
    }

    /**
     * Invokes window for choosing file with specific filters
     * @return window for choosing file
     */
    public FileChooser fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        //possible to add more extensions
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt"));
        return fileChooser;
    }

    /**
     * Opens fileChooser and lets user choose file to open
     * @return file chosen by user to open
     */
    public File chooseFileToOpen() {
        FileChooser fileChooser = fileChooser();
        fileChooser.setTitle("Open");
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Opens fileChooser and lets user choose file to save current session
     * @return file chosen by user to save current session
     */
    public File chooseFileToSave() {
        FileChooser fileChooser = fileChooser();
        fileChooser.setTitle("Save");
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * Method that handles close request
     * @param event
     */
    public void closeWindowRequest(WindowEvent event) {
        if (saveBeforeClosing()) {
            return;
        }
        event.consume();
    }

    /**
     * Method for handling process after clicking "New" option in File tab in menu
     * - if current session has not been saved yet, saveBeforeClosing() method gets called
     * @return boolean value whether New action was handled correctly
     */
    public boolean newAction() {
        if (saveBeforeClosing()) {
            updateStatus(null, true);
            clearTextFields();
            clearNodes();
            setButtonsTextFieldsStartup(menuItemInsert);
            return true;
        }
        return false;
    }

    /**
     * Method for handling process after clicking "Open" option in File tab in menu
     * - if current session has not been saved yet, saveBeforeClosing() method gets called
     * @return boolean whether Open action was handled correctly (false = either some error occurred or file to Open does not exit)
     */
    public boolean openAction() {
        if (saveBeforeClosing()) {
            File fileToOpen = chooseFileToOpen();
            if (fileToOpen == null) {
                return false;
            }
            else {
                try {
                    //read and display graph
                    updateStatus(fileToOpen, false);
                    clearTextFields();
                    clearNodes();
                    setButtonsTextFieldsStartup(menuItemInsert);
                }
                catch (Exception e) {
                    errorAction(Action.OPEN);
                }
                return true;
            }
        }
        else return false;
    }

    /**
     * Method for handling process after clicking "Save" option in File tab in menu
     * - if file has not been saved yet, saveAsAction() gets called instead
     * - else content of current session gets written into file
     * @return boolean value whether Save action was handled correctly (false = iff saveAsAction() returns false)
     */
    public boolean saveAction() {
        if (file == null) {
            return saveAsAction();
        }
        else {
            try {
                // write to file graph representation
                //writeToFile
                updateStatus(file, false);
                clearTextFields();
                clearNodes();
                setButtonsTextFieldsStartup(menuItemInsert);
            }
            catch (Exception e) {
                errorAction(Action.SAVE);
            }
            return true;
        }

    }

    /**
     * Method for handling process after clicking "Save As" option in File tab in menu
     * - firstly, fileChooser window opens for user to choose file to save current session into
     * - current session is saved into chosen file afterwards
     * @return boolean value whether Save As action was handled correctly (false = either file to save does not exit or error occurred)
     */
    public boolean saveAsAction() {
        File fileToSave = chooseFileToSave();
        if (fileToSave == null)
            return false;
        else {
            try {
                //write to chosen file graph representation
                //writeToFile()
                updateStatus(fileToSave, false);
                clearTextFields();
                clearNodes();
                setButtonsTextFieldsStartup(menuItemInsert);
            }
            catch (Exception e) {
                errorAction(Action.SAVE);
            }
            return true;
        }
    }

    /**
     * Method for handling "Exit" option in File tab in menu
     * - window closes iff file current session is saved (or if there was no change)
     */
    public void exitAction() {
        if (saveBeforeClosing()) {
            clearTextFields();
            clearNodes();
            setButtonsTextFieldsStartup(menuItemInsert);
            Platform.exit();
        }
    }

    /**
     * Method for handling errors that occur during Actions from File tab in menu
     * @param action which specifies during which Action from File tab the error occurred
     */
    private void errorAction(Action action) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try {
            alert.setTitle("Error");
            String errorName = action.name().toLowerCase();
            alert.setContentText("There was an error during " + errorName + " action!");
        }
        catch (Exception e) {
            alert.setContentText("There was an error during error action!");
        }
        finally {
            //ButtonType OK = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
            //alert.getButtonTypes().add(OK);

            Optional<ButtonType> actionResult = alert.showAndWait();
            if (actionResult.isPresent()) {
                alert.close();
            }
        }

    }

    /**
     * Method that clears textfields
     */
    public void clearTextFields() {
        for (Pair<String, TextField> textField : listOfTextFields) {
            textField.getSecond().clear();
        }
    }

    /**
     * Method that resets canvas and deletes nodes internally
     */
    public void clearNodes() {
        vertices.clear();
        canvas.getChildren().clear();
    }

    /**
     * Method that sets buttons and textfields to disabled in startup
     */
    public void setButtonsTextFieldsStartup(RadioMenuItem insert) {
        for (Triplet<String, Button, Mode> mode : listOfButtons) {
            if (!mode.getFirst().equals(insert.getText()) && mode.getSecond() != null) {
                mode.getSecond().setDisable(true);
            }
            if (insert.getText().equals(mode.getFirst())) {
                mode.getSecond().setDisable(false);
            }
        }
        for (Pair<String, TextField> textField : listOfTextFields) {
            if (!textField.getFirst().equals(insert.getText()) && textField.getSecond() != null) {
                textField.getSecond().setDisable(true);
            }
            if (insert.getText().equals(textField.getFirst())) {
                textField.getSecond().setDisable(false);
            }
        }
        insert.setSelected(true);
    }

    /**
     * Method that returns current mode that application is running in
     * @return mode
     */
    public Mode getCurrentMode() {
        RadioMenuItem currentMode = (RadioMenuItem) modes.getSelectedToggle();
        for (Pair<String, Mode> mode : listOfModes) {
            if (currentMode.getText().equals(mode.getFirst())) {
                return mode.getSecond();
            }
        }
        return null;
    }

    /**
     * Method for updating status of session if canvas change occurred
     */
    public void handleCanvasChange() {
        if (!changedStatus) {
            updateStatus(file, true);
        }
    }


    @Override
    public void start(Stage stage) {
        this.stage = stage;

        updateStatus(null, true);
        //root node
        BorderPane pane = new BorderPane();

        Scene scene = new Scene(pane, defaultWindowWidth, defaultWindowHeight);
        //css styling of background
        //pane.setStyle("-fx-background-color: #DADADA");

        /* Canvas - graph display
         * - pref size: scene size - Vbox size
         * - simple bordering around the edges of canvas
         * - white background
         * - canvas size changes according to window size changes, and so is border
         * - added listener for changes in canvas (list of nodes)
         */
        this.canvas = new Pane();
        canvas.setPrefSize(scene.getWidth() - 200, scene.getHeight() - 200);
        Border border = new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        canvas.setBorder(border);
        canvas.setStyle("-fx-background-color: white");

        scene.widthProperty().addListener((observableValue, number, t1) -> {
            canvas.setMinWidth(scene.getWidth() - 200);
            canvas.setMaxWidth(scene.getWidth() - 200);
            canvas.setBorder(border);
        });
        scene.heightProperty().addListener((observableValue, number, t1) -> {
            canvas.setMinHeight(scene.getHeight());
            canvas.setMaxHeight(scene.getHeight());
            canvas.setBorder(border);
        });
        canvas.getChildren().addListener((ListChangeListener<Node>) change -> handleCanvasChange());

        /*
         * Vbox
         * - default size: height = 200, width = 200
         * - spacing between elements set to 10
         * - 10pt margins around the vbox, aligned to top and center
         * - addVertexButton
         * - textField for addVertexButton
         */
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        Button addVertex = new Button("Add vertex");
        TextField textFieldAddVertex = new TextField();
        textFieldAddVertex.setPromptText("Enter vertex value to insert");

        Button deleteVertex = new Button("Delete vertex");
        TextField textFieldDeleteVertex = new TextField();
        textFieldDeleteVertex.setPromptText("Enter vertex value to delete");
        vBox.setMaxWidth(200);
        vBox.setMinWidth(200);
        vBox.getChildren().addAll(addVertex, textFieldAddVertex, deleteVertex, textFieldDeleteVertex);
        vBox.setAlignment(Pos.TOP_CENTER);

        PseudoClass emptyTextField = PseudoClass.getPseudoClass("emptyTextField");
        textFieldAddVertex.pseudoClassStateChanged(emptyTextField, textFieldAddVertex.getText().isEmpty());
        textFieldDeleteVertex.pseudoClassStateChanged(emptyTextField, textFieldDeleteVertex.getText().isEmpty());
        textFieldAddVertex.textProperty().isEmpty().addListener((observableValue, wasEmpty, isEmpty) ->
                textFieldAddVertex.pseudoClassStateChanged(emptyTextField, isEmpty));
        textFieldDeleteVertex.textProperty().isEmpty().addListener((observableValue, wasEmpty, isEmpty) ->
                textFieldDeleteVertex.pseudoClassStateChanged(emptyTextField, isEmpty));


        /* MenuBar
         * 4 tabs: File, Mode, Customization, Help
         * - File: possible actions
         * -- New: Opens new file
         * -- Save: Save file
         * -- Save as: Save file as
         * -- Open: Open file
         * -- Exit: Exit application
         *
         * - Mode: toggles
         * -- insert: changes app mode for user to only use insert button
         * -- delete: changes app mode for user to only use delete button
         * -- addEdge: changes app mode for user to only use addEdge button
         * - default: at start, insert toggle is selected
         *
         * - Customization: not implemented yet, but possibly in the future for better UX
         *
         * - Help: for manual
         */
        MenuBar menuBar = new MenuBar();
        Menu mFile = new Menu("File");
        MenuItem menuItemNew = new MenuItem("New");
        MenuItem menuItemSave = new MenuItem("Save");
        MenuItem menuItemOpen = new MenuItem("Open");
        MenuItem menuItemExport = new MenuItem("Save as");
        MenuItem menuItemExit = new MenuItem("Exit");

        Menu mModes = new Menu("Mode");
        modes = new ToggleGroup();
        listOfModes = new ArrayList<>();
        listOfModes.add(new Pair<>("Insert Nodes...",Mode.INSERT));
        listOfModes.add(new Pair<>("Delete Nodes... (partially implemented)",Mode.DELETE));
        listOfModes.add(new Pair<>("Add Edges... (not implemented)", Mode.ADDEDGE));

        listOfButtons = new ArrayList<>();
        Triplet<String, Button, Mode> insert = new Triplet<>("Insert Nodes...", addVertex, Mode.INSERT);
        listOfButtons.add(insert);
        Triplet<String, Button, Mode> delete = new Triplet<>("Delete Nodes...", deleteVertex, Mode.DELETE);
        listOfButtons.add(delete);
        Triplet<String, Button, Mode> addEdge = new Triplet<>("Add Edges... (not implemented)", null, Mode.ADDEDGE);
        listOfButtons.add(addEdge);
        menuItemInsert = new RadioMenuItem(insert.getFirst());
        RadioMenuItem menuItemDelete = new RadioMenuItem(delete.getFirst());
        RadioMenuItem menuItemEdges = new RadioMenuItem(addEdge.getFirst());
        menuItemEdges.setDisable(true);
        menuItemInsert.setToggleGroup(modes);
        menuItemDelete.setToggleGroup(modes);
        menuItemEdges.setToggleGroup(modes);
        mModes.getItems().addAll(menuItemInsert, menuItemDelete, menuItemEdges);

        /*
         * Pairs for clearing textFields
         */
        listOfTextFields = new ArrayList<>();
        listOfTextFields.add(new Pair<>(insert.getFirst(), textFieldAddVertex));
        listOfTextFields.add(new Pair<>(delete.getFirst(), textFieldDeleteVertex));

        //default - insert selected, other disabled and cleared
        menuItemInsert.setSelected(true);
        setButtonsTextFieldsStartup(menuItemInsert);

        Menu mCustomization = new Menu("Customization");
        RadioMenuItem menuItemLight = new RadioMenuItem("Light Mode");
        RadioMenuItem menuItemDark = new RadioMenuItem("Dark Mode (not implemented)");
        mCustomization.getItems().addAll(menuItemDark, menuItemLight);
        //default - light mode selected
        menuItemLight.setSelected(true);

        Menu mHelp = new Menu("Help");
        MenuItem openManual = new MenuItem("Open manual");
        openManual.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
        openManual.setOnAction(actionEvent -> {
            try {
                Manual manual = new Manual();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mHelp.getItems().add(openManual);
        mFile.getItems().addAll(menuItemNew, menuItemSave, menuItemOpen, menuItemExport, new SeparatorMenuItem(), menuItemExit);
        menuBar.getMenus().addAll(mFile, mModes, mCustomization, mHelp);

        /*
         * Label for showing, which mode is currently being used
         * - placed on top-right of canvas
         */
        RadioMenuItem selectedForLabel = (RadioMenuItem) modes.getSelectedToggle();
        Label modeSelected = new Label("Mode selected: " + selectedForLabel.getText());
        modeSelected.setLayoutX(10);
        modeSelected.setLayoutY(5);
        canvas.getChildren().add(modeSelected);

        /*
         * Label for showing last action
         */
        Label lastAction = new Label("Last action: ");
        canvas.getChildren().add(lastAction);
        //no binds yet TODO
        lastAction.setLayoutX(10);
        lastAction.setLayoutY(canvasHeight - 60);

        /*
         * shortcuts for some actions
         * - CTRL + N - New window
         * - CTRL + M - Open manual
         * - CTRL + O - Open file (load)
         * - CTRL + S - Save
         * - CTRL + Shift + S - Save as
         * - ALT+F4 - Exit application
         */
        menuItemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuItemExport.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        menuItemExit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        /*
         * Actions when clicked on certain button/tab
         */
        menuItemNew.setOnAction(actionEvent -> {
            newAction(); //can ignore the return value
        });
        menuItemSave.setOnAction(actionEvent -> {
            saveAction();//errorAction(Action.SAVE);
        });
        menuItemOpen.setOnAction(actionEvent -> {
            openAction();//errorAction(Action.OPEN);
        });
        menuItemExport.setOnAction(actionEvent -> {
            saveAsAction(); //errorAction(Action.SAVEAS);
        });
        menuItemExit.setOnAction(actionEvent -> exitAction());
        modes.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (modes.getSelectedToggle() != null) {
                RadioMenuItem radioMenuItem = (RadioMenuItem) modes.getSelectedToggle();
                modeSelected.setText("Mode selected: " + radioMenuItem.getText());
                //disable all other buttons
                for (Triplet<String, Button, Mode> button : listOfButtons) {
                    if (!button.getFirst().equals(radioMenuItem.getText()) && button.getSecond() != null) {
                        button.getSecond().setDisable(true);
                    }
                    else if (button.getSecond() != null) button.getSecond().setDisable(false);
                }
                for (Pair<String, TextField> textField : listOfTextFields) {
                    if (!textField.getFirst().equals(radioMenuItem.getText()) && textField.getSecond() != null) {
                        textField.getSecond().setDisable(true);
                        textField.getSecond().clear();

                    }
                    else if (textField.getSecond() != null) textField.getSecond().setDisable(false);
                }
            }
        });

        vertices = new HashMap<>();
        Random rnd = new Random(); //for generating random coordinates of node

        /*
         * addVertexButton + dragging functionality of nodes
         */
        addVertex.setOnAction(actionEvent -> {
            AtomicReference<Double> dragX = new AtomicReference<>((double) 0);
            AtomicReference<Double> dragY = new AtomicReference<>((double) 0);
            Text value = new Text(textFieldAddVertex.getText());
            Vertex newVertex = new Vertex(value, 0,0);
            if (!vertices.containsKey(value.getText())) {
                Stack<Vertex> verticesWithValue = new Stack<>();
                verticesWithValue.push(newVertex);
                vertices.put(value.getText(), verticesWithValue);
            }
            else {
                vertices.get(value.getText()).add(newVertex);
            }
            //System.out.println(vertices);
            //2*radius offset so it isnt generated outside of canvas
            newVertex.setTranslateX(((double) 1/2) * canvasWidth);
            newVertex.setTranslateY(((double) 2/3) * canvasHeight);
            canvas.getChildren().add(newVertex);
            lastAction.setText("Last action: Inserted node with value \"" + newVertex.getString() + "\"");
            newVertex.setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    newVertex.getScene().setCursor(Cursor.HAND);
                }
            });

            newVertex.setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    newVertex.getScene().setCursor(Cursor.DEFAULT);
                }
            });

            newVertex.setOnMousePressed(mouseEvent -> {
                if (mouseEvent.isPrimaryButtonDown()) {
                    newVertex.getScene().setCursor(Cursor.DEFAULT);
                }
                dragX.set(mouseEvent.getX());
                dragY.set(mouseEvent.getY());
                newVertex.getScene().setCursor(Cursor.MOVE);
            });

            newVertex.setOnMouseReleased(mouseEvent -> {
                newVertex.getScene().setCursor(Cursor.HAND);
                newVertex.resetFill();
            });

            newVertex.setOnMouseDragged(mouseEvent -> {
                double newX = newVertex.getLayoutX() + mouseEvent.getX() - dragX.get();
                double newY = newVertex.getLayoutY() + mouseEvent.getY() - dragY.get();

                if (newX > canvasWidth || newY > canvasHeight) {
                    return;
                }
                //TODO add boundaries for moving nodes out of the canvas

                newVertex.setVertexFill(Color.RED);
                newVertex.setLayoutX(newX);
                newVertex.setLayoutY(newY);
            });

            //test, whether click responds
            newVertex.setOnMouseClicked(mouseEvent -> {
            });

        });

        /*
         * deletes first
         */
        deleteVertex.setOnAction(actionEvent -> {
            String valueOfNode = textFieldDeleteVertex.getText();
            StringBuilder labelText = new StringBuilder();
            if (vertices.containsKey(valueOfNode)) {
                labelText.append("Last action: Deleted node with value \"").append(valueOfNode).append("\"");
                Vertex vertexToRemove = vertices.get(valueOfNode).pop();
                canvas.getChildren().remove(vertexToRemove);
                if (vertices.get(valueOfNode).size() == 0) vertices.remove(valueOfNode);
            }
            else {
                labelText.append("Last action: Failed to delete node with value \"").append(valueOfNode).append("\"");
                //open dialog, that node with that value does not appear in canvas
            }
            lastAction.setText(labelText.toString());
        });


        /*
         * positioning of nodes on screen
         */
        pane.setTop(menuBar);
        pane.setLeft(canvas);
        pane.setRight(vBox);


        scene.getStylesheets().add("styles.css");
        pane.setId("rootPane");
        modeSelected.setId("modeSelected");


        /*
         * stage format
         */
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("Graph Editor");
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(true);
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.getIcons().add(new Image("file:icons/icon2.png"));
        stage.show();
        stage.setOnCloseRequest(this::closeWindowRequest);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
