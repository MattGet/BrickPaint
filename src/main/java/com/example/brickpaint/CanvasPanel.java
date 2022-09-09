package com.example.brickpaint;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

public class CanvasPanel {

    private AnchorPane parent;

    public CanvasPanel(AnchorPane anchorPane){ parent = anchorPane;}

    public AnchorPane root = new AnchorPane();
    public Canvas canvas = new Canvas();
    public ImageView imageView = new ImageView();

    public ScrollPane scrollPane;

    private AnimatedZoomOperator operator;

    public void Setup(BrickKeys keys){
        root.getChildren().add(canvas);
        root.getChildren().add(imageView);
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(1000);
        scrollPane.setPrefViewportWidth(1920);
        parent.getChildren().add(scrollPane);

        root.setOnScroll(this::onScroll);
        root.setOnMouseDragged(this::onDrag);

        operator = new AnimatedZoomOperator(keys);
    }


    /**
     * Handles moving/panning the canvasPanel with the AnimatedZoomOperator class
     * @param event
     */
    public void onDrag(MouseEvent event){
        double zoomFactor = 1.5;
        if (event.getY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }
        operator.pan(root, zoomFactor, event.getSceneX(), event.getSceneY());
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

}
