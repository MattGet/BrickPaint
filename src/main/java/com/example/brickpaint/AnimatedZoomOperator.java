package com.example.brickpaint;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Handles methods that manipulate the position and size of nodes within javafx
 * @author Aerus
 * @author matde
 * @see <a href="https://stackoverflow.com/a/29545368">Source Code</a>
 */
public class AnimatedZoomOperator {

    /**
     * timeline variable for animating nodes within this class's methods
     */
    private final Timeline timeline;


    /**
     * default constructor
     */
    public AnimatedZoomOperator() {
        this.timeline = new Timeline(60);
    }

    /**
     * handles animating/scaling a node within a 2d axis based on mouse input
     * @param node
     * @param factor
     * @param x
     * @param y
     */
    public void zoom(Node node, double factor, double x, double y) {
        // determine scale
        double oldScale = node.getScaleX();
        double scale = clamp( oldScale * factor, 0.01, 1);
        double f = (scale / oldScale) - 1;

        // determine offset that we will have to move the node
        //Bounds bounds = node.localToScene(node.getBoundsInLocal());
        //double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        //double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(300), new KeyValue(node.scaleYProperty(), scale))
        );
        timeline.play();
    }

    /**
     * handles animating/moving a node across a 2d axis based on mouse input
     * @param node
     * @param factor
     * @param x
     * @param y
     */
    public void pan(Node node, double factor, double x, double y){
        // determine scale
        double oldScale = node.getScaleX();
        double scale = clamp( oldScale * factor, 0.01, 1);
        double f = (scale / oldScale) - 1;

        // determine offset that we will have to move the node
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        double moveX = f * dx;
        double moveY = f * dy;


        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(),  node.getTranslateX() + moveX)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), node.getTranslateY() + moveY))
        );
        timeline.play();
    }

    /**
     * Used to restrict a double value to a certain range of values
     * @param val
     * @param min
     * @param max
     * @return Value between Min and Max limits
     */
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}