package com.example.brickpaint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Pair;

public class CanvasPanel {

    private Pair<Double, Double> initialTouch;
    private BrickPaintController controller;
    private AnchorPane parent;


    /**
     * Default Constructor
     * @param anchorPane
     * @param controllerIn
     */
    public CanvasPanel(AnchorPane anchorPane, BrickPaintController controllerIn){ parent = anchorPane;
        controller = controllerIn;}

    public StackPane root = new StackPane();
    public Canvas canvas = new Canvas();
    public ImageView imageView = new ImageView();

    public ScrollPane scrollPane;

    private AnimatedZoomOperator operator;
    private Line tempLine;
    private GraphicsContext gc;

    public void Setup(BrickKeys keys){

        //root.getChildren().add(imageView);
        root.getChildren().add(canvas);
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(1520);
        scrollPane.setPrefViewportHeight(650);
        parent.getChildren().add(scrollPane);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        root.setOnScroll(this::onScroll);
        root.setOnMouseDragged(this::onDrag);
        root.setOnMousePressed(this::onMousePressed);
        root.setOnMouseReleased(this::onMouseDragOver);

        operator = new AnimatedZoomOperator(keys);
        gc = canvas.getGraphicsContext2D();
    }

    public void onMousePressed(MouseEvent event){
        initialTouch = new Pair<>(event.getX(), event.getY());
    }
    public void onMouseDragOver(MouseEvent event){
        if (controller.getToolType() == 1){
            gc.strokeLine(initialTouch.getKey(), initialTouch.getValue(), event.getX(), event.getY());
            System.out.print("test");
        }
    }



    /**
     * Handles any events which require the OnDrag event on the canvas
     * @param event
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
    }

    /**
     * Handles zooming/scaling the canvasPanel with the AnimatedZoomOperator class
     * @param event
     */
    public void onScroll (ScrollEvent event){
        double zoomFactor = 1.5;
        if (event.getDeltaY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }

        operator.zoom(root, zoomFactor, event.getSceneX(), event.getSceneY());
    }

    private void addLine(double x, double y) {
        tempLine = new Line(initialTouch.getKey(), initialTouch.getValue(), x, y);
        root.getChildren().add(tempLine);
    }
}
