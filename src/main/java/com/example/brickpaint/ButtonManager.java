package com.example.brickpaint;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;

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

    private final HashMap<ToggleButton, SelectionListener> toggleButtonToSelectionListener = new HashMap<>();
    private final ToggleButton tPointer, tPencil, tRainbow, tEraser, tLine, tRect, tRRect, tSquare, tCircle, tEllipse,
            tPolygon, tCustom, tGrabber, tSelect;

    public final ToggleSwitch tAutoSave;

    public Label aSaveTime = new Label("Auto Save In 1m 25s");
    private final Button tClipboard, tCut, tCopy, tCrop, tFlipV, tFlipH;
    public ComboBox<Integer> lineWidth = new ComboBox<>();
    public ChoiceBox<BrickTools> lineStyle = new ChoiceBox<>();
    /**
     * allows the user to enter or select a value for the current canvas's width
     */
    public ComboBox<Double> cWidth = new ComboBox<>();
    /**
     * allows the user to enter or select a value for the current canvas's height
     */
    public ComboBox<Double> cHeight = new ComboBox<>();
    /**
     * allows the user to enter or select a value for the number of sides the polygon tools should have
     */
    public ComboBox<Integer> polySides = new ComboBox<>();
    public ColorPicker colorPicker = new ColorPicker(Color.BLACK);
    private final ToolBar Parent;
    private final BrickPaintController controller;
    private final List<ToggleButton> toggles;

    /**
     * Default Constructor for the Button Manager, creates all the buttons with parameters and lays them out
     * in a predefined manner
     *
     * @param parent The toolbar to create the buttons under
     * @param cont The controller class in charge of the application
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
        HBox h1 = new HBox(tSelect, tCrop);
        h1.setSpacing(10);
        h1.paddingProperty().setValue(new Insets(10, 0, 10, 0));
        HBox h2 = new HBox(tFlipV, tFlipH);
        h2.setSpacing(10);

        Label bImage = new Label("Selection");
        bImage.paddingProperty().setValue(new Insets(15, 0, 0, 0));
        VBox image = new VBox(h1, h2, bImage);
        image.setAlignment(Pos.BASELINE_CENTER);

        Separator s2 = new Separator(Orientation.VERTICAL);

        tPointer = new ToggleButton();
        tPointer.setGraphic(getImage(poin));
        tPointer.setTooltip(new Tooltip("Mouse Pointer"));
        tEraser = new ToggleButton();
        tEraser.setGraphic(getImage(eras));
        tEraser.setTooltip(new Tooltip("Eraser"));
        HBox v1 = new HBox(tPointer, tEraser);
        v1.paddingProperty().setValue(new Insets(10, 0, 0, 0));
        v1.setSpacing(10);

        tPencil = new ToggleButton();
        tPencil.setGraphic(getImage(penc));
        tPencil.setTooltip(new Tooltip("Pencil"));
        tRainbow = new ToggleButton();
        tRainbow.setGraphic(getImage(rain));
        tRainbow.setTooltip(new Tooltip("Rainbow Pencil"));
        HBox v2 = new HBox(tPencil, tRainbow);
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

        lineWidth.getItems().addAll(1, 2, 4, 8, 10, 12, 14, 18, 24, 30, 36, 48, 60, 72);
        lineWidth.setValue(10);
        lineWidth.setEditable(true);
        lineWidth.setMaxWidth(100);
        lineStyle.getItems().addAll(BrickTools.SolidLine, BrickTools.DashedLine);
        lineStyle.setValue(BrickTools.SolidLine);
        lineStyle.setMaxWidth(100);
        lineStyle.setPrefWidth(100);
        polySides.getItems().addAll(3, 4, 5, 6, 7, 8, 10, 12, 16, 20, 24, 36);
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

        cWidth.getItems().addAll(256d, 512d, 720d, 1024d, 1080d, 1280d, 1440d, 1920d);
        cHeight.getItems().addAll(256d, 512d, 720d, 1024d, 1080d, 1280d, 1440d, 1920d);
        cWidth.setEditable(true);
        cHeight.setEditable(true);
        cWidth.setMaxWidth(100);
        cHeight.setMaxWidth(100);
        Label lCH = new Label("Canvas Height   ");
        Label lCW = new Label("Canvas Width    ");
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

        Label bColor = new Label("Color");
        bColor.paddingProperty().setValue(new Insets(13, 0, 0, 0));
        colorPicker.setMinHeight(30);
        VBox color = new VBox(colorPicker, v11, bColor);
        color.setSpacing(10);
        color.setAlignment(Pos.BASELINE_CENTER);

        Separator s7 = new Separator(Orientation.VERTICAL);

        tAutoSave = new ToggleSwitch("AutoSave");
        tAutoSave.setTooltip(new Tooltip("Turn AutoSave On or Off"));

        Label bSave = new Label("Save");
        bSave.paddingProperty().setValue(new Insets(35, 0, 0, 0));
        VBox save = new VBox(tAutoSave, aSaveTime, bSave);
        save.setSpacing(10);
        save.setAlignment(Pos.BASELINE_CENTER);

        HBox everything = new HBox(clipBoard, s1, image, s2, tools, s3, shapes, s4, style, s5, color, s6, canvas, s7, save);
        everything.setSpacing(10);
        parent.getItems().addAll(everything);
        toggles = new ArrayList<>() {{
            add(tPointer);
            add(tPencil);
            add(tRainbow);
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
        }};
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

        tAutoSave.setSelected(true);
        TimerTask saveManager = new AutoSaveManager(75, this);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(saveManager, 0, 1000);
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
    public void handlePaste(ActionEvent event){
        controller.getCanvas().selectionPaste();
    }

    /**
     * Calls the cut method in the current canvas when the paste button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCut(ActionEvent event){
        if (getSelectedToggle() == BrickTools.SelectionTool){
            controller.getCanvas().selectionCut();
        }
        else{
            tSelect.setSelected(true);
        }
    }

    /**
     * Calls the copy method in the current canvas when the copy button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCopy(ActionEvent event){
        if (getSelectedToggle() == BrickTools.SelectionTool){
            controller.getCanvas().selectionCopy();
        }
        else{
            tSelect.setSelected(true);
        }
    }

    /**
     * Calls the crop method in the current canvas when the crop button is pressed and the selection tool is active,
     * otherwise it will set the selection tool to be active
     *
     * @param event Button event
     */
    public void handleCrop(ActionEvent event){
        if (getSelectedToggle() == BrickTools.SelectionTool){
            controller.getCanvas().selectionCrop();
        }
        else{
            tSelect.setSelected(true);
        }
    }

    /**
     * Sets the current selected tool to the standard mouse pointer
     */
    public void resetToggles() {
        tPointer.setSelected(true);
    }

    /**
     * Changes the mouse cursor based on the selected tool
     */
    public void changeCursor() {
        switch (getSelectedToggle()){
            case Pointer -> {
                Parent.getScene().setCursor(Cursor.DEFAULT);
            }
            case Pencil -> {
                ImageCursor cursor = new ImageCursor(penc, 0, penc.getHeight());
                Parent.getScene().setCursor(cursor);
            }
            case RainbowPencil -> {
                ImageCursor cursor = new ImageCursor(rain, 0, rain.getHeight());
                Parent.getScene().setCursor(cursor);
            }
            case Eraser -> {
                ImageCursor cursor = new ImageCursor(eras, 0, eras.getHeight());
                Parent.getScene().setCursor(cursor);
            }
            case ColorGrabber -> {
                ImageCursor cursor = new ImageCursor(grab, 0, grab.getHeight());
                Parent.getScene().setCursor(cursor);
            }
            case SelectionTool, Line, Rectangle, RoundRectangle, Square, Circle, Oval, Polygon, CustomShape -> {
                Parent.getScene().setCursor(Cursor.CROSSHAIR);
            }
        }

    }

    /**
     * Updates the current canvas's width when the user changes its value
     */
    protected void handleCWidth(ActionEvent event) {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            controller.getCanvas().setSizeX(numb);
            //System.out.println("Set Width");
        } catch (Exception e) {
            System.out.println("Canvas Width input was Invalid");
        }
    }

    /**
     * Updates the current canvas's height when the user changes its value
     */
    protected void handleCHeight(ActionEvent event) {
        try {
            String value = cHeight.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            controller.getCanvas().setSizeY(numb);
            //System.out.println("Set Height");
        } catch (Exception e) {
            System.out.println("Canvas Height input was Invalid");
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
            case 4 -> {
                return BrickTools.Eraser;
            }
            case 5 -> {
                return BrickTools.Line;
            }
            case 6 -> {
                return BrickTools.Rectangle;
            }
            case 7 -> {
                return BrickTools.RoundRectangle;
            }
            case 8 -> {
                return BrickTools.Square;
            }
            case 9 -> {
                return BrickTools.Circle;
            }
            case 10 -> {
                return BrickTools.Oval;
            }
            case 11 -> {
                return BrickTools.Polygon;
            }
            case 12 -> {
                return BrickTools.CustomShape;
            }
            case 13 -> {
                return BrickTools.ColorGrabber;
            }
            case 14 -> {
                return BrickTools.SelectionTool;
            }
            default -> {
                return BrickTools.Nothing;
            }
        }
    }

    public void AutoSave(){
        controller.saveAll();
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

        public void invalidated(Observable observable) {
            if (this.toggleButton.isSelected()) {
                for (ToggleButton toggle : toggles) {
                    if (toggle != this.toggleButton) toggle.setSelected(false);
                }
            }
        }
    }
}
