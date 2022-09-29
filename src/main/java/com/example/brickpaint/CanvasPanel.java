package com.example.brickpaint;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Manages all the drawing Sub-nodes for the overarching canvas, may have multiple instances to create multiple canvases
 *
 * @author matde
 */
public class CanvasPanel {

    /**
     * The Name of the Panel
     */
    public final String Name;
    /**
     * Instance of the controller class for this canvas
     */
    private final BrickPaintController controller;
    /**
     * The parent Pane of this Canvas
     */
    private final TabPane parent;

    private final SnapshotParameters parameters = new SnapshotParameters();
    private final Double cWidth = 1024d;
    private final Double cHeight = 1024d;
    /**
     * The root Node that all of the canvas components are created under
     */
    public StackPane root = new StackPane();
    /**
     * The main canvas that will be used for drawing ect.
     */
    public Canvas canvas = new Canvas();
    /**
     * The viewport (scrollpane) that the entire canvas panel will reside within
     */
    public ScrollPane scrollPane;
    /**
     * A dummy canvas used to draw objects with a live preview before they are drawn on the real canvas
     */
    public Canvas sketchCanvas = new Canvas();
    private final GraphicsContext sc = sketchCanvas.getGraphicsContext2D();
    /**
     * Top level pane of the actual canvas objects and their corresponding stack pane
     */
    public AnchorPane pane = new AnchorPane();
    /**
     * Coordinates of the most recent mouse press
     */
    private Point2D initialTouch;
    /**
     * Coordinates of the mouse
     */
    private Point2D currTouch;
    /**
     * The instance of the zoom operator class that controls the movement and scale of this canvas panel
     */
    private AnimatedZoomOperator operator;
    /**
     * Manages Undo and Redo operations on this canvas
     */
    public UndoManager undoManager;
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private int curr = 0;

    private final List<Point2D> polyLine = new ArrayList<>();
    private boolean drawingPolyLine = false;

    /**
     * Default Constructor
     *
     * @param tPane        The parent of this class
     * @param name         The Name of the Canvas Panel
     * @param keys         The instance of BrickKeys used for keybinds
     * @param controllerIn The controller class for this canvas
     */
    public CanvasPanel(TabPane tPane, String name, BrickKeys keys, BrickPaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Name = name;
        Setup(keys, name);
    }

    /**
     * Configures all of the settings of this Canvas Panel, should be called immediately after instantiation
     *
     * @param keys The instance of BickKyes that this class should use
     * @param name The name of the CanvasPanel
     */
    public void Setup(BrickKeys keys, String name) {

        sketchCanvas.setHeight(cHeight);
        sketchCanvas.setWidth(cWidth);
        canvas.setHeight(cHeight);
        canvas.setWidth(cWidth);

        AnchorPane canvasPane = new AnchorPane(canvas);
        AnchorPane sCPane = new AnchorPane(sketchCanvas);

        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        AnchorPane.setLeftAnchor(sketchCanvas, 0.0);
        AnchorPane.setRightAnchor(sketchCanvas, 0.0);
        AnchorPane.setTopAnchor(sketchCanvas, 0.0);
        AnchorPane.setBottomAnchor(sketchCanvas, 0.0);

        root.setPrefWidth(cWidth);
        root.setPrefHeight(cHeight);
        root.getChildren().add(canvasPane);
        root.getChildren().add(sCPane);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        pane.getChildren().add(root);
        pane.setPrefHeight(cHeight);
        pane.setPrefWidth(cWidth);
        pane.getStyleClass().add("border");

        scrollPane = new ScrollPane(pane);
        scrollPane.setPrefViewportWidth(cWidth);
        scrollPane.setPrefViewportHeight(cHeight);
        Tab tab = new Tab(name, scrollPane);
        parent.getTabs().add(tab);
        AnchorPane.setBottomAnchor(scrollPane, (double) 0);
        AnchorPane.setTopAnchor(scrollPane, (double) 0);
        AnchorPane.setRightAnchor(scrollPane, (double) 0);
        AnchorPane.setLeftAnchor(scrollPane, (double) 0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        UpdateSize();
        pane.setOnScroll(this::onScroll);
        pane.setOnMouseDragged(this::onDrag);
        pane.setOnMousePressed(this::onMousePressed);
        pane.setOnMouseReleased(this::onMouseReleased);
        pane.setOnMouseMoved(this::onMove);

        parameters.setFill(Color.TRANSPARENT);

        operator = new AnimatedZoomOperator(keys);
        undoManager = new UndoManager();
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * Updates the size number of the current canvas in the toolbar
     */
    public void UpdateSize() {
        controller.buttonManager.cHeight.getEditor().setText(String.valueOf(canvas.getHeight()));
        controller.buttonManager.cWidth.getEditor().setText(String.valueOf(canvas.getWidth()));
    }

    /**
     * Sets the width of all necessary nodes in the canvas panel
     *
     * @param x new width
     */
    public void setSizeX(double x) {
        canvas.setWidth(x);
        sketchCanvas.setWidth(x);
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
    }

    /**
     * Sets the height of all necessary nodes in the canvas panel
     *
     * @param y new height
     */
    public void setSizeY(double y) {
        canvas.setHeight(y);
        sketchCanvas.setHeight(y);
        root.setPrefHeight(y);
        pane.setPrefHeight(y);
    }


    /**
     * Handles all actions that should occur when the mouse is pressed down, also records the pointer position
     *
     * @param event Mouse Event from Input class
     */
    private void onMousePressed(MouseEvent event) {
        initialTouch = new Point2D(event.getX(), event.getY());
        if (event.getButton() == MouseButton.PRIMARY) {
            if (controller.getToolType() != BrickTools.Pointer) {
                undoManager.setMark();
                initDraw(canvas.getGraphicsContext2D());
                initDraw(sketchCanvas.getGraphicsContext2D());
            }
            if (controller.getToolType() == BrickTools.Pencil || controller.getToolType() == BrickTools.RainbowPencil ||
                    controller.getToolType() == BrickTools.Eraser) {
                undoManager.LogU(this);
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());
                gc.stroke();
            }
            if (controller.getToolType() == BrickTools.ColorGrabber) {
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage snap = canvas.snapshot(parameters, null);
                PixelReader reader = snap.getPixelReader();
                controller.buttonManager.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
                root.getScene().setCursor(Cursor.DEFAULT);
                controller.buttonManager.resetGrabber();
            }
            if (controller.getToolType() == BrickTools.CustomShape) {
                initDraw(canvas.getGraphicsContext2D());
                initDraw(sketchCanvas.getGraphicsContext2D());
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                if (polyLine.size() > 0) {
                    for (Point2D point : polyLine) {
                        //System.out.println("XComp = " + ArtMath.compare(point.getX(), event.getX(), 10d));
                        //System.out.println("YComp = " + ArtMath.compare(point.getY(), event.getY(), 10d));
                        if (ArtMath.compare(point.getX(), event.getX(), 10d) && ArtMath.compare(point.getY(), event.getY(), 10d)) {
                            gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), point.getX(), point.getY());
                            drawingPolyLine = false;
                            polyLine.clear();
                            System.out.println("Matched point");
                            return;
                        }
                        else drawingPolyLine = true;
                    }
                    gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                } else {
                    //System.out.println("First point");
                    undoManager.LogU(this);
                    drawingPolyLine = true;
                }
                polyLine.add(initialTouch);
            }
        }
        if (event.getButton() == MouseButton.SECONDARY){
            if (controller.getToolType() == BrickTools.CustomShape) {
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                if (polyLine.size() <= 1) {
                    drawingPolyLine = false;
                    polyLine.clear();
                }
                else{
                    gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                    polyLine.clear();
                    drawingPolyLine = false;
                }
            }
        }
    }

    /**
     * Handles all actions that should occur when the mouse is released
     *
     * @param event Mouse Event from Input class
     */
    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            switch (controller.getToolType()) {
                case Line -> {
                    undoManager.LogU(this);
                    gc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Rectangle -> {
                    undoManager.LogU(this);
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case RoundRectangle -> {
                    undoManager.LogU(this);
                    ArtMath.DrawRoundedRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Square -> {
                    undoManager.LogU(this);
                    ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Circle -> {
                    undoManager.LogU(this);
                    ArtMath.DrawCircle(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Oval -> {
                    undoManager.LogU(this);
                    ArtMath.DrawOval(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Polygon -> {
                    undoManager.LogU(this);
                    int sides = 6;
                    try {
                        sides = BrickPaintController.clamp(Integer.parseInt(controller.buttonManager.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ArtMath.DrawPoly(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sides, gc);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                default -> {
                }
            }
        }
    }


    /**
     * Sets the parameters for drawing in the specified graphics content
     *
     * @param gc The graphics context to set parameters for
     */
    private void initDraw(GraphicsContext gc) {
        gc.setFill(controller.buttonManager.colorPicker.getValue());
        gc.setStroke(controller.buttonManager.colorPicker.getValue());
        double width = Double.parseDouble(controller.buttonManager.lineWidth.getEditor().getText());
        gc.setLineWidth(width);
        if (controller.buttonManager.lineStyle.getValue() == BrickTools.DashedLine) {
            //System.out.println("Setting dashed");
            gc.setLineDashes(width * 2, width * 2, width * 2, width * 2);
        } else {
            gc.setLineDashes(0d);
        }

        if (controller.getToolType() == BrickTools.Pencil || controller.getToolType() == BrickTools.RainbowPencil) {
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            BoxBlur blur = new BoxBlur();
            blur.setWidth(width / 4);
            blur.setHeight(width / 4);
            blur.setIterations(1);
            gc.setEffect(blur);
        } else if (controller.getToolType() == BrickTools.Eraser) {
            gc.setEffect(null);
        } else if (controller.getToolType() == BrickTools.CustomShape){
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
        }
        else {
            gc.setLineCap(StrokeLineCap.SQUARE);
            gc.setEffect(null);
        }
    }

    /**
     * Clears the canvas panel
     */
    public void clearAll() {
        undoManager.LogU(this);
        gc.setEffect(null);
        sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Handles any actions which require the OnMove mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onMove(MouseEvent event) {
        if (controller.getToolType() == BrickTools.ColorGrabber) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage snap = canvas.snapshot(parameters, null);
            PixelReader reader = snap.getPixelReader();
            controller.buttonManager.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
        }
        if (controller.getToolType() == BrickTools.CustomShape){
            if (drawingPolyLine) {
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                if (polyLine.size() > 1) {
                    for (Point2D point : polyLine) {
                        if (ArtMath.compare(point.getX(), event.getX(), 10d) && ArtMath.compare(point.getY(), event.getY(), 10d)) {
                            sc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), point.getX(), point.getY());
                            return;
                        }
                    }
                    sc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                }
                else {
                    sc.strokeLine(polyLine.get(0).getX(), polyLine.get(0).getY(), event.getX(), event.getY());
                }
            }
        }
    }


    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onDrag(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            currTouch = new Point2D(event.getX(), event.getY());
            //System.out.println(controller.getToolType());
            switch (controller.getToolType()) {
                case Pointer -> {
                    double zoomFactor = 1.5;
                    if (event.getY() <= 0) {
                        // zoom out
                        zoomFactor = 1 / zoomFactor;
                    }
                    operator.pan(pane, zoomFactor, event.getSceneX(), event.getSceneY());
                }
                case RainbowPencil -> {
                    if (curr >= 5) {
                        gc.beginPath();
                        curr = 0;
                    } else curr++;
                    gc.setStroke(new Color(Math.random(), Math.random(), Math.random(), 1));
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
                case Pencil -> {
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
                case Eraser -> {
                    double size = controller.buttonManager.lineWidth.getValue() * 2;
                    gc.clearRect(event.getX() - size / 2, event.getY() - size / 2, size, size);
                }
                case Line -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    sc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                }
                case Rectangle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                }
                case RoundRectangle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRoundedRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                }
                case Square -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                }
                case Circle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawCircle(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                }
                case Oval -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawOval(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                }
                case Polygon -> {
                    int sides = 6;
                    try {
                        sides = BrickPaintController.clamp(Integer.parseInt(controller.buttonManager.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawPoly(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sides, sc);
                }
                default -> {
                }
            }
        }
    }

    /**
     * Handles zooming/scaling the canvasPanel with the AnimatedZoomOperator class
     *
     * @param event Scroll event from Input class
     */
    public void onScroll(ScrollEvent event) {
        double zoomFactor = 1.5;
        if (event.getDeltaY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }
        operator.zoom(pane, zoomFactor, event.getSceneX(), event.getSceneY());
        UpdateSize();
    }
}
