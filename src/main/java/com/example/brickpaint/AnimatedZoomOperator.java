package com.example.brickpaint;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Handles methods that manipulate the position and size of nodes within javafx
 *
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
     * Input Manager class instance, helps with managing keybinds ect
     */
    private final BrickKeys Keys;

    /**
     * default constructor
     *
     * @param keys The instance of BrickKeys used for keybinds
     */
    public AnimatedZoomOperator(BrickKeys keys) {
        Keys = keys;
        this.timeline = new Timeline(60);
    }

    /**
     * Used to restrict a double value to a certain range of values
     *
     * @param val The value to evaluate
     * @param min The minimum value this function can return
     * @param max The maximum value this function can return
     * @return Value between Min and Max limits
     */
    public static double clamp(double val, double min, double max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }

    /**
     * handles animating/scaling a node within a 2d axis based on mouse input
     *
     * @param node   The node to zoom in/out
     * @param factor Scaling value that determines how much the node is scaled with each call
     * @param x      X cord from which to zoom about (Optional)
     * @param y      Y cord from which to zoom about (Optional)
     */
    public void zoom(Node node, double factor, double x, double y) {
        if (!Keys.activeKeys.getActiveKeys().contains(KeyCode.CONTROL)) {
            return;
        }
        // determine scale
        double oldScale = node.getScaleX();
        double scale = clamp(oldScale * factor, 0.01, 10);
        if (x < 0)
            scale /= factor;
        else
            scale *= factor;

        double f = (scale / oldScale)-1;

        double dx = (x - (node.getBoundsInParent().getWidth()/2 + node.getBoundsInParent().getMinX()));
        double dy = (y - (node.getBoundsInParent().getHeight()/2 + node.getBoundsInParent().getMinY()));

        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), node.getTranslateX() - f * dx)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.translateYProperty(), node.getTranslateY() - f * dy)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleYProperty(), scale))
        );
        timeline.play();
    }

    /**
     * handles animating/moving a node across a 2d axis based on mouse input
     *
     * @param node   The node to move/pan
     * @param factor Scaling factor that determines how much the node is moved each time
     * @param x      X cord of the mouse pointer
     * @param y      Y cord of the mouse pointer
     */
    public void pan(Node node, double factor, double x, double y) {
        if (!Keys.activeKeys.getActiveKeys().contains(KeyCode.CONTROL)) {
            return;
        }
        // determine offset that we will have to move the node
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

        //Bounds parent = node.getParent().localToScene(node.getParent().getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));


        //System.out.println("Bounds X = " + (bounds.getWidth() / 2 + bounds.getMinX()));
        //System.out.println("Bounds Y = " + (bounds.getHeight() / 2 + bounds.getMinY()));
        //System.out.println("X = " + x + " Y = " + y);
        //System.out.println("moveX = " + moveX + "moveY = " + moveY);


        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), node.getTranslateX() + dx)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), node.getTranslateY() + dy))
        );
        timeline.play();
    }
}