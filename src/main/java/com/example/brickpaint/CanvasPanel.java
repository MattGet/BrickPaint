package com.example.brickpaint;

import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;


/**
 * Manages all the drawing Sub-nodes for the overarching canvas, may have multiple instances to create multiple canvases
 *
 * @author matde
 */
public class CanvasPanel {

    /**
     * Instance of the controller class for this canvas
     */
    private final BrickPaintController controller;
    /**
     * The parent Pane of this Canvas
     */
    private final TabPane parent;
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
    private UndoManager undoManager;

    public Canvas sketchCanvas = new Canvas();

    public AnchorPane pane = new AnchorPane();

    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private  GraphicsContext sc = sketchCanvas.getGraphicsContext2D();


    //private GraphicsContext gc;
    private final SnapshotParameters parameters = new SnapshotParameters();
    private boolean isIntial = false;
    /**
     * Default Constructor
     *
     * @param tPane  The parent of this class
     * @param controllerIn The controller class for this canvas
     */
    public CanvasPanel(TabPane tPane, BrickKeys keys, BrickPaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Setup(keys);
    }

    /**
     * Configures all of the settings of this Canvas Panel, should be called immediately after instantiation
     *
     * @param keys The instance of BickKyes that this class should use
     */
    public void Setup(BrickKeys keys) {

        sketchCanvas.setHeight(630);
        sketchCanvas.setWidth(1520);
        canvas.setHeight(630);
        canvas.setWidth(1520);

        AnchorPane canvasPane = new AnchorPane(canvas);
        canvasPane.setStyle("-fx-background-color: lightgrey;");
        AnchorPane sCPane = new AnchorPane(sketchCanvas);

        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        AnchorPane.setLeftAnchor(sketchCanvas, 0.0);
        AnchorPane.setRightAnchor(sketchCanvas, 0.0);
        AnchorPane.setTopAnchor(sketchCanvas, 0.0);
        AnchorPane.setBottomAnchor(sketchCanvas, 0.0);

        root.setPrefWidth(1520);
        root.setPrefHeight(630);
        root.getChildren().add(canvasPane);
        root.getChildren().add(sCPane);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        pane.getChildren().add(root);
        pane.setPrefHeight(630);
        pane.setPrefWidth(1520);

        scrollPane = new ScrollPane(pane);
        scrollPane.setPrefViewportWidth(1520);
        scrollPane.setPrefViewportHeight(630);
        parent.getTabs().add(new Tab("Untitled Project", scrollPane));
        AnchorPane.setBottomAnchor(scrollPane, (double) 0);
        AnchorPane.setTopAnchor(scrollPane, (double) 0);
        AnchorPane.setRightAnchor(scrollPane, (double) 0);
        AnchorPane.setLeftAnchor(scrollPane, (double) 0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        UpdateSize();

        pane.setOnScroll(this::onScroll);
        pane.setOnMouseDragged(this::onDrag);
        pane.setOnMousePressed(this::onMousePressed);
        pane.setOnMouseReleased(this::onMouseReleased);

        parameters.setFill(Color.TRANSPARENT);

        operator = new AnimatedZoomOperator(keys);
        undoManager = new UndoManager();
        gc = canvas.getGraphicsContext2D();
    }

    public void UpdateSize(){
        controller.cHeight.setText(String.valueOf(canvas.getHeight()));
        controller.cWidth.setText(String.valueOf(canvas.getWidth()));
    }

    public void setSizeX(double x){
        canvas.setWidth(x);
        sketchCanvas.setWidth(x);
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
    }
    public void setSizeY(double y){
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
    public void onMousePressed(MouseEvent event) {
        initialTouch = new Point2D(event.getX(), event.getY());
        isIntial = true;
        switch (controller.getToolType()){
            case 1:
            case 2:
            case 3:
                undoManager.setMark();
                initDraw(canvas.getGraphicsContext2D());
                initDraw(sketchCanvas.getGraphicsContext2D());
                break;
            default:
                break;
        }
    }

    /**
     * Handles all actions that should occur when the mouse is released
     *
     * @param event Mouse Event from Input class
     */
    public void onMouseReleased(MouseEvent event) {
        switch (controller.getToolType()) {
            case 1:
                gc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                break;
            case 2:
                ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            case 3:
                ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc);
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            default:
                break;
        }
    }


    /**
     * Sets the parameters for drawing in the specified graphics content
     *
     * @param gc The graphics context to set parameters for
     */
    private void initDraw(GraphicsContext gc) {
        gc.setFill(controller.colorPicker.getValue());
        gc.setStroke(controller.colorPicker.getValue());
        gc.setLineWidth((int) controller.lineWidth.getValue());
    }

    public void clearAll(){
        sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
        gc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
    }


    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    public void onDrag(MouseEvent event) {
        currTouch = new Point2D(event.getX(), event.getY());
        switch (controller.getToolType()){
            case 0:
                double zoomFactor = 1.5;
                if (event.getY() <= 0) {
                    // zoom out
                    zoomFactor = 1 / zoomFactor;
                }
                operator.pan(pane, zoomFactor, event.getSceneX(), event.getSceneY());
                break;
            case 1:
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                sc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                break;
            case 2:
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                break;
            case 3:
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc);
                break;
            default:
                break;
        }

        isIntial = false;
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
