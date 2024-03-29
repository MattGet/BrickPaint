package com.example.brickpaint;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.example.brickpaint.BrickPaintController.clamp;

/**
 * Sets up all toolbar buttons for the application as well as their graphics, layout, listeners, and methods
 *
 * @author matde
 */
public class ButtonManager {
    private static final Image paste = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/clipboard-icon.png")));
    private static final Image poin = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/pointer-icon.png")));
    private static final Image penc = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/pencil-icon.png")));
    private static final Image rain = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/rainbow.png")));
    private static final Image eras = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/eraser.png")));
    private static final Image lin = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Line.png")));
    private static final Image rec = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/rectangle.png")));
    private static final Image rrec = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/RRectangle.png")));
    private static final Image squa = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/square.png")));
    private static final Image circ = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/circle.png")));
    private static final Image elli = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/oval.png")));
    private static final Image poly = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/polygon.png")));
    private static final Image cust = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/customPoly.png")));
    private static final Image grab = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/dropper.png")));
    private static final Image selc = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Cursor-Select-icon.png")));
    private static final Image cut = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/cut-icon.png")));
    private static final Image copy = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/copy.png")));
    private static final Image crop = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Editing-Crop-icon.png")));
    private static final Image vflip = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Actions-object-flip-vertical-icon.png")));
    private static final Image hflip = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Actions-object-flip-horizontal-icon.png")));

    private static final Image rRight = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/right_rotate_icon.png")));
    private static final Image rLeft = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/left_rotate_icon.png")));
    private static final Image bucket = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/bucket.png")));
    private static final Image text = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/text_icon.png")));
    public final ToggleSwitch tAutoSave, tFillShapes;
    private final HashMap<ToggleButton, SelectionListener> toggleButtonToSelectionListener = new HashMap<>();
    private final ToggleButton tPointer, tPencil, tRainbow, tEraser, tLine, tRect, tRRect, tSquare, tCircle, tEllipse,
            tPolygon, tCustom, tGrabber, tSelect, tBucket, tMakeServer, tConnectToServer, tSmartErase, tText;
    private final Button tClipboard, tCut, tCopy, tCrop, tFlipV, tFlipH, tRright, tRleft, tOpenFolder;
    private final ToolBar Parent;
    private final BrickPaintController controller;
    private final List<ToggleButton> toggles;
    private final ObservableList<Integer> DEFUALTRESOLUTIONS =
            FXCollections.observableList(Arrays.asList(256, 512, 720, 1024, 1080, 1280, 1440, 1920));
    private final BobRoss bobby;
    public Label aSaveTime = new Label("Auto Save In 1m 25s");
    public ComboBox<Integer> lineWidth =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(1, 2, 4, 8, 10, 12, 14, 18, 24, 30, 36, 48, 60, 72)));
    public ChoiceBox<BrickTools> lineStyle = new ChoiceBox<>();
    /**
     * allows the user to enter or select a value for the current canvas's width
     */
    public ComboBox<Integer> cWidth = new ComboBox<>();
    /**
     * allows the user to enter or select a value for the current canvas's height
     */
    public ComboBox<Integer> cHeight = new ComboBox<>();
    /**
     * allows the user to enter or select a value for the number of sides the polygon tools should have
     */
    public ComboBox<Integer> polySides =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(3, 4, 5, 6, 7, 8, 10, 12, 16, 20, 24, 36)));
    public Spinner<Double> fillSensitivity = new Spinner<>(0.0d, 0.9d, 0.25d, 0.05d);
    public ColorPicker colorPicker = new ColorPicker(Color.BLACK);
    private TimerTask saveManager;
    private Timer timer;
    private boolean updatingServer = false;

    /**
     * Default Constructor for the Button Manager, creates all the buttons with parameters and lays them out
     * in a predefined manner
     *
     * @param parent The toolbar to create the buttons under
     * @param cont   The controller class in charge of the application
     */
    public ButtonManager(ToolBar parent, BrickPaintController cont) {
        Parent = parent;
        controller = cont;

        tClipboard = new Button();
        tClipboard.setGraphic(getImage(paste));
        tClipboard.setTooltip(new Tooltip("Paste (Ctrl + V)"));
        tCut = new Button();
        tCut.setGraphic(getImage(cut));
        tCut.setTooltip(new Tooltip("Cut (Ctrl + X"));
        tCopy = new Button();
        tCopy.setGraphic(getImage(copy));
        tCopy.setTooltip(new Tooltip("Copy (Ctrl + C)"));
        Label bClip = new Label("Clipboard");

        VBox cc = new VBox(tCut, tCopy);
        cc.setSpacing(10);
        HBox clipB = new HBox(tClipboard, cc);
        clipB.setSpacing(10);
        clipB.setAlignment(Pos.CENTER);
        clipB.paddingProperty().setValue(new Insets(10, 10, 15, 10));

        VBox clipBoard = new VBox(clipB, bClip);
        clipBoard.setAlignment(Pos.BASELINE_CENTER);

        Separator s1 = new Separator(Orientation.VERTICAL);

        tSelect = new ToggleButton();
        tSelect.setGraphic(getImage(selc));
        tSelect.setTooltip(new Tooltip("Selection Tool"));
        tCrop = new Button();
        tCrop.setGraphic(getImage(crop));
        tCrop.setTooltip(new Tooltip("Crop to Selection"));
        tFlipV = new Button();
        tFlipV.setGraphic(getImage(vflip));
        tFlipV.setTooltip(new Tooltip("Flip selection vertically"));
        tFlipH = new Button();
        tFlipH.setGraphic(getImage(hflip));
        tFlipH.setTooltip(new Tooltip("Flip selection horizontally"));
        tRright = new Button();
        tRright.setGraphic(getImage(rRight));
        tRright.setTooltip(new Tooltip("Rotate Image right 90"));
        tRleft = new Button();
        tRleft.setGraphic(getImage(rLeft));
        tRleft.setTooltip(new Tooltip("Rotate Image left 90"));
        HBox h1 = new HBox(tSelect, tFlipV, tRright);
        h1.setSpacing(10);
        h1.paddingProperty().setValue(new Insets(10, 0, 10, 0));
        HBox h2 = new HBox(tCrop, tFlipH, tRleft);
        h2.setSpacing(10);

        Label bImage = new Label("Selection");
        bImage.paddingProperty().setValue(new Insets(15, 0, 0, 0));
        VBox image = new VBox(h1, h2, bImage);
        image.setAlignment(Pos.BASELINE_CENTER);

        Separator s2 = new Separator(Orientation.VERTICAL);

        fillSensitivity.setMaxWidth(65);
        fillSensitivity.setTooltip(new Tooltip("Fill Sensitivity"));
        tPointer = new ToggleButton();
        tPointer.setGraphic(getImage(poin));
        tPointer.setTooltip(new Tooltip("Mouse Pointer"));
        tEraser = new ToggleButton();
        tEraser.setGraphic(getImage(eras));
        tEraser.setTooltip(new Tooltip("Eraser"));
        tSmartErase = new ToggleButton();
        tSmartErase.setGraphic(getImage(eras));
        tSmartErase.setTooltip(new Tooltip("Smart Eraser"));
        HBox v1 = new HBox(tPointer, tEraser, tSmartErase, fillSensitivity);
        v1.paddingProperty().setValue(new Insets(10, 0, 0, 0));
        v1.setSpacing(10);

        tPencil = new ToggleButton();
        tPencil.setGraphic(getImage(penc));
        tPencil.setTooltip(new Tooltip("Pencil"));
        tRainbow = new ToggleButton();
        tRainbow.setGraphic(getImage(rain));
        tRainbow.setTooltip(new Tooltip("Rainbow Pencil"));
        tText = new ToggleButton();
        tText.setGraphic(getImage(text));
        tText.setTooltip(new Tooltip("Text Tool"));
        tBucket = new ToggleButton();
        tBucket.setGraphic(getImage(bucket));
        tBucket.setTooltip(new Tooltip("Bucket Fill"));
        tBucket.setMaxWidth(65);
        tBucket.setPrefWidth(65);
        HBox v2 = new HBox(tPencil, tRainbow, tText, tBucket);
        v2.setSpacing(10);

        Label bTools = new Label("Tools");
        bTools.paddingProperty().setValue(new Insets(5, 0, 0, 0));
        VBox tools = new VBox(v1, v2, bTools);
        tools.setSpacing(10);
        tools.setAlignment(Pos.BASELINE_CENTER);

        Separator s3 = new Separator(Orientation.VERTICAL);

        tLine = new ToggleButton();
        tLine.setGraphic(getImage(lin));
        tLine.setTooltip(new Tooltip("Line"));
        tRect = new ToggleButton();
        tRect.setGraphic(getImage(rec));
        tRect.setTooltip(new Tooltip("Rectangle"));
        tRRect = new ToggleButton();
        tRRect.setGraphic(getImage(rrec));
        tRRect.setTooltip(new Tooltip("Rounded Rectangle"));
        tSquare = new ToggleButton();
        tSquare.setGraphic(getImage(squa));
        tSquare.setTooltip(new Tooltip("Square"));
        HBox v3 = new HBox(tLine, tRect, tRRect, tSquare);
        v3.paddingProperty().setValue(new Insets(10, 0, 10, 0));
        v3.setSpacing(5);

        tCircle = new ToggleButton();
        tCircle.setGraphic(getImage(circ));
        tCircle.setTooltip(new Tooltip("Circle"));
        tEllipse = new ToggleButton();
        tEllipse.setGraphic(getImage(elli));
        tEllipse.setTooltip(new Tooltip("Ellipse"));

        tPolygon = new ToggleButton();
        tPolygon.setGraphic(getImage(poly));
        tPolygon.setTooltip(new Tooltip("Polygon"));
        tCustom = new ToggleButton();
        tCustom.setGraphic(getImage(cust));
        tCustom.setTooltip(new Tooltip("Custom Shape"));
        HBox v5 = new HBox(tCircle, tEllipse, tPolygon, tCustom);
        v5.setSpacing(5);

        Label bShapes = new Label("Shapes");
        bShapes.paddingProperty().setValue(new Insets(15, 0, 0, 0));
        VBox shapes = new VBox(v3, v5, bShapes);
        shapes.setAlignment(Pos.BASELINE_CENTER);

        Separator s4 = new Separator(Orientation.VERTICAL);

        lineWidth.setValue(10);
        lineWidth.setEditable(true);
        lineWidth.setMaxWidth(100);
        lineStyle.getItems().addAll(BrickTools.SolidLine, BrickTools.DashedLine);
        lineStyle.setValue(BrickTools.SolidLine);
        lineStyle.setMaxWidth(100);
        lineStyle.setPrefWidth(100);
        polySides.setValue(6);
        polySides.setEditable(true);
        polySides.setMaxWidth(100);
        Label lPS = new Label("Polygon Sides    ");
        Label lW = new Label("Line Width         ");
        Label lS = new Label("Line Style           ");
        HBox v6 = new HBox(lW, lineWidth);
        HBox v7 = new HBox(lS, lineStyle);
        HBox v10 = new HBox(lPS, polySides);
        Label bStyle = new Label("Brush Style");
        bStyle.paddingProperty().setValue(new Insets(0, 0, 0, 0));
        VBox style = new VBox(v10, v6, v7, bStyle);
        style.setSpacing(5);
        style.setAlignment(Pos.BASELINE_CENTER);

        Separator s5 = new Separator(Orientation.VERTICAL);

        cWidth.getItems().addAll(DEFUALTRESOLUTIONS);
        cHeight.getItems().addAll(DEFUALTRESOLUTIONS);
        cWidth.setEditable(true);
        cHeight.setEditable(true);
        cWidth.setMaxWidth(100);
        cHeight.setMaxWidth(100);
        Label lCH = new Label("Height   ");
        Label lCW = new Label("Width    ");
        HBox v8 = new HBox(lCW, cWidth);
        v8.setAlignment(Pos.CENTER);
        v8.paddingProperty().setValue(new Insets(15, 0, 0, 0));
        HBox v9 = new HBox(lCH, cHeight);
        v9.setAlignment(Pos.CENTER);

        Label bCanvas = new Label("Canvas");
        bCanvas.paddingProperty().setValue(new Insets(5, 0, 0, 0));
        VBox canvas = new VBox(v8, v9, bCanvas);
        canvas.setSpacing(10);
        canvas.setAlignment(Pos.BASELINE_CENTER);

        Separator s6 = new Separator(Orientation.VERTICAL);

        tGrabber = new ToggleButton();
        tGrabber.setGraphic(getImage(grab));
        Label lCG = new Label("Select Color       ");
        HBox v11 = new HBox(lCG, tGrabber);
        v11.setAlignment(Pos.BASELINE_CENTER);
        v11.translateYProperty().setValue(-5);

        tFillShapes = new ToggleSwitch("Fill Shapes");
        tFillShapes.setTooltip(new Tooltip("Will fill any drawn shapes with the selected color"));

        Label bColor = new Label("Color");
        bColor.paddingProperty().setValue(new Insets(5, 0, 0, 0));
        colorPicker.setMinHeight(25);
        colorPicker.translateYProperty().setValue(-10);
        VBox color = new VBox(colorPicker, v11, tFillShapes, bColor);
        color.setSpacing(5);
        color.setAlignment(Pos.BASELINE_CENTER);

        Separator s7 = new Separator(Orientation.VERTICAL);

        tAutoSave = new ToggleSwitch("AutoSave");
        tAutoSave.setTooltip(new Tooltip("Turn AutoSave On or Off"));
        tOpenFolder = new Button("Open Images Folder");
        tOpenFolder.setTooltip(new Tooltip("Opens the folder where saved images are stored by default"));

        Label bSave = new Label("Save");
        bSave.paddingProperty().setValue(new Insets(-2, 0, 0, 0));
        VBox save = new VBox(tAutoSave, aSaveTime, tOpenFolder, bSave);
        save.setSpacing(10);
        save.setAlignment(Pos.BASELINE_CENTER);

        Separator s8 = new Separator(Orientation.VERTICAL);

        tMakeServer = new ToggleButton("Host Server");
        tMakeServer.setSelected(false);
        tMakeServer.setTooltip(new Tooltip("Host a Server that connects other BrickPaint users over your LAN"));

        tConnectToServer = new ToggleButton("Connect to Server");
        tConnectToServer.setSelected(false);
        tConnectToServer.setTooltip(new Tooltip("Connect to a BrickPaint Server on your LAN and Collaborate with others!"));


        Label bServer = new Label("[ALPHA] BrickServer");
        bServer.paddingProperty().setValue(new Insets(17, 0, 0, 0));
        VBox server = new VBox(tMakeServer, tConnectToServer, bServer);
        server.setSpacing(10);
        server.setAlignment(Pos.BASELINE_CENTER);

        parent.setStyle("-fx-spacing: 10px;");
        parent.getItems().addAll(clipBoard, s1, image, s2, tools, s3, shapes, s4, style, s5, color, s6, canvas, s7, save, s8, server);
        toggles = new ArrayList<>() {{
            add(tPointer);
            add(tPencil);
            add(tRainbow);
            add(tText);
            add(tSmartErase);
            add(tEraser);
            add(tLine);
            add(tRect);
            add(tRRect);
            add(tSquare);
            add(tCircle);
            add(tEllipse);
            add(tPolygon);
            add(tCustom);
            add(tGrabber);
            add(tSelect);
            add(tBucket);
        }};

        bobby = new BobRoss(this);
        Setup();
    }

    /**
     * Helper function to create an imageview of size 20x20
     *
     * @param image The image to add to the imageView
     * @return 20x20 ImageView
     */
    private static ImageView getImage(Image image) {
        ImageView curr = new ImageView(image);
        curr.setFitHeight(20);
        curr.setFitWidth(20);
        return curr;
    }

    /**
     * sets up all the event listeners for the buttons and toggle buttons
     */
    private void Setup() {
        for (ToggleButton toggleButton : toggles) {
            SelectionListener selectionListener = new SelectionListener(toggleButton);
            this.toggleButtonToSelectionListener.put(toggleButton, selectionListener);
            toggleButton.selectedProperty().addListener(selectionListener);
        }
        tPointer.setSelected(true);
        cWidth.setOnAction(this::handleCWidth);
        cHeight.setOnAction(this::handleCHeight);
        tCopy.setOnAction(this::handleCopy);
        tCut.setOnAction(this::handleCut);
        tClipboard.setOnAction(this::handlePaste);
        tCrop.setOnAction(this::handleCrop);
        tFlipV.setOnAction(this::handleFlipV);
        tFlipH.setOnAction(this::handleFlipH);
        tRright.setOnAction(this::handleRotateR);
        tRleft.setOnAction(this::handleRotateL);
        tOpenFolder.setOnAction(this::handleOpenFolder);
        tMakeServer.setOnAction(this::handleServerToggle);
        tConnectToServer.setOnAction(this::handleClientToggle);

        tAutoSave.setSelected(true);
        startAutoSave();
    }

    /**
     * Will start the auto save thread if not already started, if it exists already it will cancel the thread's
     * scheduled execution and create a new one, essentially resetting the autosave timer.
     */
    public void startAutoSave() {
        try {
            if (timer == null) timer = new Timer();
            if (saveManager == null) saveManager = new AutoSaveManager(300, this);
            timer.scheduleAtFixedRate(saveManager, 0, 1000);
            BrickPaintController.logger.info("[APP] Started AutoSave Thread");
        } catch (Exception e) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            saveManager = new AutoSaveManager(300, this);
            timer.scheduleAtFixedRate(saveManager, 0, 1000);
            BrickPaintController.logger.info("[APP] Restarted AutoSave Thread");
        }
    }

    /**
     * Will return the currently selected toggle button as an enum type
     *
     * @return type BrickTools
     */
    public BrickTools getSelectedToggle() {
        for (int i = 0; i <= toggles.size() - 1; i++) {
            if (toggles.get(i).isSelected()) {
                return posToTool(i);
            }
        }
        return BrickTools.Nothing;
    }

    /**
     * Calls the paste method in the current canvas when the paste button is pressed
     *
     * @param event Button event
     */
    public void handlePaste(ActionEvent event) {
        controller.getCanvas().selectionPaste();
    }

    /**
     * Calls the cut method in the current canvas when the paste button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCut(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionCut();
        } else {
            tSelect.setSelected(true);
        }
    }

    /**
     * Calls the copy method in the current canvas when the copy button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCopy(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionCopy();
        } else {
            tSelect.setSelected(true);
        }
    }

    /**
     * Calls the crop method in the current canvas when the crop button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCrop(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionCrop();
        } else {
            tSelect.setSelected(true);
        }
    }

    /**
     * Will mirror the selected area across the y axis or if no area is selected it will mirror the entire canvas
     *
     * @param event Button event
     */
    public void handleFlipV(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionMirror(false);
        } else {
            controller.getCanvas().mirror(false);
        }
    }

    /**
     * Will mirror the selected area across the x axis or if no area is selected it will mirror the entire canvas
     *
     * @param event Button event
     */
    public void handleFlipH(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionMirror(true);
        } else {
            controller.getCanvas().mirror(true);
        }
    }

    /**
     * Rotates the selected image 90 degrees to the right or if no area is selected will rotate the entire canvas
     *
     * @param event Button event
     */
    public void handleRotateR(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionRotate(true);
        } else {
            controller.getCanvas().rotate(true);
        }
    }

    /**
     * Rotates the selected image 90 degrees to the left or if no area is selected will rotate the entire canvas
     *
     * @param event Button event
     */
    public void handleRotateL(ActionEvent event) {
        if (getSelectedToggle() == BrickTools.SelectionTool) {
            controller.getCanvas().selectionRotate(false);
        } else {
            controller.getCanvas().rotate(false);
        }
    }

    /**
     * Opens the default images directory in the user's file explorer
     *
     * @param event Button event
     */
    public void handleOpenFolder(ActionEvent event) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(BrickSave.savePath));
            BrickPaintController.logger.info("[APP] Opened Images Folder");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the current selected tool to the standard mouse pointer
     */
    public void resetToggles() {
        tPointer.setSelected(true);
        this.changeCursor();
    }

    /**
     * Changes the mouse cursor based on the selected tool
     */
    public void changeCursor() {
        switch (getSelectedToggle()) {
            case Pointer -> {
                Parent.getScene().setCursor(Cursor.DEFAULT);
            }
            case Pencil -> {
                ImageCursor cursor = new ImageCursor(penc, 0, penc.getHeight() / 1.5);
                Parent.getScene().setCursor(cursor);
            }
            case RainbowPencil -> {
                ImageCursor cursor = new ImageCursor(rain, 0, rain.getHeight() / 1.5);
                Parent.getScene().setCursor(cursor);
            }
            case Eraser -> {
                ImageCursor cursor = new ImageCursor(eras, 0, eras.getHeight() / 1.5);
                Parent.getScene().setCursor(cursor);
            }
            case ColorGrabber -> {
                ImageCursor cursor = new ImageCursor(grab, 0, grab.getHeight() / 1.5);
                Parent.getScene().setCursor(cursor);
            }
            case BucketFill -> {
                ImageCursor cursor = new ImageCursor(bucket, bucket.getWidth() / 1.25, bucket.getHeight() / 1.25);
                Parent.getScene().setCursor(cursor);
            }
            case Text, SmartEraser, SelectionTool, Line, Rectangle, RoundRectangle, Square, Circle, Oval, Polygon, CustomShape -> {
                Parent.getScene().setCursor(Cursor.CROSSHAIR);
            }
        }

    }

    /**
     * Updates the current canvas's width when the user changes its value
     *
     * @param event Button Event
     */
    protected void handleCWidth(ActionEvent event) {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            controller.getCanvas().setSizeX(numb);
            //System.out.println("Set Width");
        } catch (Exception e) {
            BrickPaintController.logger.error("[APP] Canvas Width input was Invalid!");
        }
    }

    /**
     * Updates the current canvas's height when the user changes its value
     *
     * @param event Button Event
     */
    protected void handleCHeight(ActionEvent event) {
        try {
            String value = cHeight.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            controller.getCanvas().setSizeY(numb);
            //System.out.println("Set Height");
        } catch (Exception e) {
            BrickPaintController.logger.error("[APP] Canvas Height input was Invalid!");
        }
    }

    /**
     * Used to convert the index of the selected tool to an enum value
     *
     * @param pos index of selected tool
     * @return enum of type BrickTools representing selected tool
     */
    private BrickTools posToTool(Integer pos) {
        switch (pos + 1) {
            case 1 -> {
                return BrickTools.Pointer;
            }
            case 2 -> {
                return BrickTools.Pencil;
            }
            case 3 -> {
                return BrickTools.RainbowPencil;
            }
            case 4 ->{
                return BrickTools.Text;
            }
            case 5 ->{
                return BrickTools.SmartEraser;
            }
            case 6 -> {
                return BrickTools.Eraser;
            }
            case 7 -> {
                return BrickTools.Line;
            }
            case 8 -> {
                return BrickTools.Rectangle;
            }
            case 9 -> {
                return BrickTools.RoundRectangle;
            }
            case 10 -> {
                return BrickTools.Square;
            }
            case 11 -> {
                return BrickTools.Circle;
            }
            case 12 -> {
                return BrickTools.Oval;
            }
            case 13 -> {
                return BrickTools.Polygon;
            }
            case 14 -> {
                return BrickTools.CustomShape;
            }
            case 15 -> {
                return BrickTools.ColorGrabber;
            }
            case 16 -> {
                return BrickTools.SelectionTool;
            }
            case 17 -> {
                return BrickTools.BucketFill;
            }
            default -> {
                return BrickTools.Nothing;
            }
        }
    }

    /**
     * Internal Method Used to call the controller method from the AutoSave Thread managed by this class
     *
     * @hidden
     */
    public void AutoSave() {
        controller.saveAll();
    }

    /**
     * Internal Method Used to update the canvas image from this class
     *
     * @param image The image to replace the canvas with
     */
    public void updateCanvas(BufferedImage image) {
        BrickImage.Paste(controller.getCanvas(), SwingFXUtils.toFXImage(image, null));
    }

    public void updateServer() {
        Image image = getCanvasImage();
        bobby.sendClientImage(image);
    }

    public void ClearAll(){
        controller.getCanvas().clearAll();
    }

    private void handleServerToggle(ActionEvent event) {
        updatingServer = true;
        if (tMakeServer.isSelected()) {
            bobby.startServer();
            tConnectToServer.setSelected(true);
        } else {
            bobby.stopServer();
            tConnectToServer.setSelected(false);
        }
        updatingServer = false;
    }

    private void handleClientToggle(ActionEvent event) {
        if (updatingServer || bobby.isServer()) return;
        if (tConnectToServer.isSelected()) {
            bobby.startClient();
        } else {
            bobby.stopClient();
        }
    }

    public void toggleClientUiSilent(boolean on) {
        updatingServer = true;
        tConnectToServer.setSelected(on);
        updatingServer = false;
    }

    public void toggleServerUiSilent(boolean on) {
        updatingServer = true;
        tMakeServer.setSelected(on);
        updatingServer = false;
    }

    /**
     * Internal Method used to fetch the canvas image from this class
     *
     * @return Image of current canvas
     */
    public Image getCanvasImage() {
        return controller.getCanvas().fetchImage();
    }

    /**
     * Tracks when a toggle button is selected and will deselect all other toggle buttons
     *
     * @author matde
     */
    private class SelectionListener implements InvalidationListener {
        private final ToggleButton toggleButton;

        public SelectionListener(ToggleButton toggleButton) {
            this.toggleButton = toggleButton;
        }

        /**
         * Will de-select all other toggles when a new toggle is selected
         *
         * @param observable Selection event
         */
        public void invalidated(Observable observable) {
            if (this.toggleButton.isSelected()) {
                for (ToggleButton toggle : toggles) {
                    if (toggle != this.toggleButton) toggle.setSelected(false);
                }
                BrickPaintController.logger.info("[APP] Changed Selected Tool To {}", getSelectedToggle());
            }
        }
    }
}
