package com.example.brickpaint;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.example.brickpaint.BrickPaintController.clamp;

public class ButtonManager {
    private static final Image clip = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/clipboard-icon.png")));
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
    private final HashMap<ToggleButton, SelectionListener> toggleButtonToSelectionListener = new HashMap<>();
    private final ToggleButton tPointer, tPencil, tRainbow, tEraser, tLine, tRect, tRRect, tSquare, tCircle, tEllipse, tPolygon, tCustom, tGrabber;
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
    private final ToggleButton tClipboard;
    private final ToggleButton tSelect;
    private ToggleButton tCrop;
    private ToggleButton tPaste;
    private ToggleButton tCut;

    public ButtonManager(ToolBar parent, BrickPaintController cont) {
        Parent = parent;
        controller = cont;
        tClipboard = new ToggleButton();
        tClipboard.setGraphic(getImage(clip));
        Label bClip = new Label("Clipboard");
        bClip.paddingProperty().setValue(new Insets(60, 0, 0, 0));
        VBox clipBoard = new VBox(tClipboard, bClip);
        clipBoard.setAlignment(Pos.BASELINE_CENTER);

        Separator s1 = new Separator(Orientation.VERTICAL);

        tSelect = new ToggleButton();
        tSelect.setGraphic(getImage(selc));
        Label bImage = new Label("Selection");
        bImage.paddingProperty().setValue(new Insets(60, 0, 0, 0));
        VBox image = new VBox(tSelect, bImage);
        image.setAlignment(Pos.BASELINE_CENTER);

        Separator s2 = new Separator(Orientation.VERTICAL);

        tPointer = new ToggleButton();
        tPointer.setGraphic(getImage(poin));
        tEraser = new ToggleButton();
        tEraser.setGraphic(getImage(eras));
        HBox v1 = new HBox(tPointer, tEraser);

        tPencil = new ToggleButton();
        tPencil.setGraphic(getImage(penc));
        tRainbow = new ToggleButton();
        tRainbow.setGraphic(getImage(rain));
        HBox v2 = new HBox(tPencil, tRainbow);

        Label bTools = new Label("Tools");
        bTools.paddingProperty().setValue(new Insets(35, 0, 0, 0));
        VBox tools = new VBox(v1, v2, bTools);
        tools.setAlignment(Pos.BASELINE_CENTER);

        Separator s3 = new Separator(Orientation.VERTICAL);

        tLine = new ToggleButton();
        tLine.setGraphic(getImage(lin));
        tRect = new ToggleButton();
        tRect.setGraphic(getImage(rec));
        tRRect = new ToggleButton();
        tRRect.setGraphic(getImage(rrec));
        tSquare = new ToggleButton();
        tSquare.setGraphic(getImage(squa));
        HBox v3 = new HBox(tLine, tRect, tRRect, tSquare);

        tCircle = new ToggleButton();
        tCircle.setGraphic(getImage(circ));
        tEllipse = new ToggleButton();
        tEllipse.setGraphic(getImage(elli));

        tPolygon = new ToggleButton();
        tPolygon.setGraphic(getImage(poly));
        tCustom = new ToggleButton();
        tCustom.setGraphic(getImage(cust));
        HBox v5 = new HBox(tCircle, tEllipse, tPolygon, tCustom);

        Label bShapes = new Label("Shapes");
        bShapes.paddingProperty().setValue(new Insets(35, 0, 0, 0));
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
        VBox style = new VBox(v6, v10, v7, bStyle);
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
        HBox v9 = new HBox(lCH, cHeight);

        Label bCanvas = new Label("Canvas");
        bCanvas.paddingProperty().setValue(new Insets(20, 0, 0, 0));
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

        HBox everything = new HBox(clipBoard, s1, image, s2, tools, s3, shapes, s4, style, s6, color, s5, canvas);
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
            add(tClipboard);
            add(tSelect);
        }};
        Setup();
    }

    private static ImageView getImage(Image image) {
        ImageView curr = new ImageView(image);
        curr.setFitHeight(20);
        curr.setFitWidth(20);
        return curr;
    }

    private void Setup() {
        for (ToggleButton toggleButton : toggles) {
            SelectionListener selectionListener = new SelectionListener(toggleButton);
            this.toggleButtonToSelectionListener.put(toggleButton, selectionListener);
            toggleButton.selectedProperty().addListener(selectionListener);
        }
        tPointer.setSelected(true);
        cWidth.setOnAction(this::handleCWidth);
        cHeight.setOnAction(this::handleCHeight);
        tGrabber.setOnAction(this::handleGrabber);
    }

    public BrickTools getSelectedToggle() {
        for (int i = 0; i <= toggles.size() - 1; i++) {
            if (toggles.get(i).isSelected()) {
                return posToTool(i);
            }
        }
        return BrickTools.Nothing;
    }

    /**
     * Sets the current selected tool to the standard mouse pointer
     */
    public void resetGrabber() {
        tGrabber.setSelected(false);
        tPointer.setSelected(true);
    }

    /**
     * Changes the mouse cursor when the grabber tool is selected
     */
    private void handleGrabber(ActionEvent event) {
        Image temp = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/dropper.png")));
        ImageCursor cursor = new ImageCursor(temp, 0, temp.getHeight());
        Parent.getScene().setCursor(cursor);
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
            default -> {
                return BrickTools.Nothing;
            }
        }
    }

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
