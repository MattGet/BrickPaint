package com.example.brickpaint;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
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

    private final BrickKeys Keys;

    /**
     * default constructor
     */
    public AnimatedZoomOperator(BrickKeys keys) {
        Keys = keys;
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
        if (!Keys.activeKeys.getActiveKeys().contains(KeyCode.CONTROL)){
            return;
        }
        y = clamp(y, 80, 1080);
        x = clamp(x, 0, 1920);
        // determine scale
        double oldScale = node.getScaleX();
        double scale = clamp( oldScale * factor, 0.01, 1);
        double f = (scale / oldScale);

        // determine offset that we will have to move the node
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        Bounds parent = node.getParent().localToScene(node.getParent().getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        double moveX = f * dx;
        double moveY = f * dy;

        //System.out.println("Bounds X = " + (bounds.getWidth() / 2 + bounds.getMinX()));
        //System.out.println("Bounds Y = " + (bounds.getHeight() / 2 + bounds.getMinY()));
        //System.out.println("X = " + x + " Y = " + y);
        //System.out.println("moveX = " + moveX + "moveY = " + moveY);


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
        if (val < min ){
            return  min;
        }
        if (val > max){
            return max;
        }
        return val;
    }
}