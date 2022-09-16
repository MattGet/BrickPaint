package com.example.brickpaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;


/**
 * Manages all the drawing Sub-nodes for the overarching canvas, may have multiple instances to create multiple canvases
 * @author matde
 */
public class CanvasPanel {

    /**
     * Cordinates of the most recent mouse press
     */
    private Pair<Double, Double> initialTouch;

    /**
     * Instance of the controller class for this canvas
     */
    private final BrickPaintController controller;

    /**
     * The parent Pane of this Canvas
     */
    private final AnchorPane parent;


    /**
     * Default Constructor
     * @param anchorPane - the parent of this class
     * @param controllerIn - the controller class for this canvas
     */
    public CanvasPanel(AnchorPane anchorPane, BrickPaintController controllerIn){ parent = anchorPane;
        controller = controllerIn;}

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
     * The instance of the zoom operator class that controls the movement and scale of this canvas panel
     */
    private AnimatedZoomOperator operator;


    //private GraphicsContext gc;

    /**
     * The canvas instance that is currently being manipulated
     */
    private Canvas currLayer;

    /**
     * Configures all of the settings of this Canvas Panel, should be called immediately after instantiation
     * @param keys - instance of BickKyes that this class should use
     */
    public void Setup(BrickKeys keys){

        //root.getChildren().add(imageView);
        root.getChildren().add(canvas);
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

        operator = new AnimatedZoomOperator(keys);
        //gc = canvas.getGraphicsContext2D();
    }

    /**
     * Handles all actions that should occur when the mouse is pressed down, also records the pointer position
     * @param event - Mouse Event from Input class
     */
    public void onMousePressed(MouseEvent event){
        initialTouch = new Pair<>(event.getX(), event.getY());
        if (controller.getToolType() == 1){
            Canvas newLayer = new Canvas(root.getWidth(), root.getHeight());
            GraphicsContext context = newLayer.getGraphicsContext2D();
            initDraw(context);

            currLayer = newLayer;
            //int temp = root.getChildren().size();
            root.getChildren().add(newLayer);
        }

    }


    /**
     * Sets the parameters for drawing in the specified graphics content
     * @param gc - the graphics context to set parameters for
     */
    private void initDraw(GraphicsContext gc){
        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.setFill(controller.colorPicker.getValue());
        gc.setStroke(controller.colorPicker.getValue());
        gc.setLineWidth((int)controller.lineWidth.getValue());
    }



    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     * @param event - Mouse Event from Input class
     */
    public void onDrag(MouseEvent event){
        if (controller.getToolType() == 0) {
            double zoomFactor = 1.5;
            if (event.getY() <= 0) {
                // zoom out
                zoomFactor = 1 / zoomFactor;
            }
            operator.pan(root, zoomFactor, event.getSceneX(), event.getSceneY());
        }
        if (controller.getToolType() == 1){
            GraphicsContext context = currLayer.getGraphicsContext2D();
            context.clearRect(0, 0, currLayer.getWidth(), currLayer.getHeight());
            context.strokeLine(initialTouch.getKey(), initialTouch.getValue(), event.getX(), event.getY());
        }
    }

    /**
     * Handles zooming/scaling the canvasPanel with the AnimatedZoomOperator class
     * @param event - Scroll event from Input class
     */
    public void onScroll (ScrollEvent event){
        double zoomFactor = 1.5;
        if (event.getDeltaY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }

        operator.zoom(root, zoomFactor, event.getSceneX(), event.getSceneY());
    }
}
