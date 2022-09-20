package com.example.brickpaint;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
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
    private final AnchorPane parent;
    /**
     * The root Node that all of the canvas components are created under
     */
    public AnchorPane root = new AnchorPane();
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
    private Pair<Double, Double> initialTouch;
    /**
     * The instance of the zoom operator class that controls the movement and scale of this canvas panel
     */
    private AnimatedZoomOperator operator;
    private UndoManager undoManager;


    //private GraphicsContext gc;
    private final SnapshotParameters parameters = new SnapshotParameters();
    private boolean isIntial = false;
    /**
     * Default Constructor
     *
     * @param anchorPane   The parent of this class
     * @param controllerIn The controller class for this canvas
     */
    public CanvasPanel(AnchorPane anchorPane, BrickPaintController controllerIn) {
        parent = anchorPane;
        controller = controllerIn;
    }

    /**
     * Configures all of the settings of this Canvas Panel, should be called immediately after instantiation
     *
     * @param keys The instance of BickKyes that this class should use
     */
    public void Setup(BrickKeys keys) {
        root.setPrefWidth(1520);
        root.setPrefHeight(650);
        root.getChildren().add(canvas);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(1520);
        scrollPane.setPrefViewportHeight(650);
        parent.getChildren().add(scrollPane);
        AnchorPane.setBottomAnchor(scrollPane, (double) 0);
        AnchorPane.setTopAnchor(scrollPane, (double) 0);
        AnchorPane.setRightAnchor(scrollPane, (double) 0);
        AnchorPane.setLeftAnchor(scrollPane, (double) 0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);


        root.setOnScroll(this::onScroll);
        root.setOnMouseDragged(this::onDrag);
        root.setOnMousePressed(this::onMousePressed);

        parameters.setFill(Color.TRANSPARENT);

        operator = new AnimatedZoomOperator(keys);
        undoManager = new UndoManager();
        //gc = canvas.getGraphicsContext2D();
    }

    /**
     * Handles all actions that should occur when the mouse is pressed down, also records the pointer position
     *
     * @param event Mouse Event from Input class
     */
    public void onMousePressed(MouseEvent event) {
        initialTouch = new Pair<>(event.getX(), event.getY());
        isIntial = true;
        if (controller.getToolType() == 1) {
            undoManager.setMark();
            initDraw(canvas.getGraphicsContext2D());
        }
    }


    /**
     * Sets the parameters for drawing in the specified graphics content
     *
     * @param gc The graphics context to set parameters for
     */
    private void initDraw(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.setFill(controller.colorPicker.getValue());
        gc.setStroke(controller.colorPicker.getValue());
        gc.setLineWidth((int) controller.lineWidth.getValue());
    }


    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    public void onDrag(MouseEvent event) {
        if (controller.getToolType() == 0) {
            double zoomFactor = 1.5;
            if (event.getY() <= 0) {
                // zoom out
                zoomFactor = 1 / zoomFactor;
            }
            operator.pan(root, zoomFactor, event.getSceneX(), event.getSceneY());
        }
        if (controller.getToolType() == 1) {
            undoManager.Undo(this);
            canvas.getGraphicsContext2D().strokeLine(initialTouch.getKey(), initialTouch.getValue(), event.getX(), event.getY());
            undoManager.Log(this);
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

        operator.zoom(root, zoomFactor, event.getSceneX(), event.getSceneY());
    }
}
