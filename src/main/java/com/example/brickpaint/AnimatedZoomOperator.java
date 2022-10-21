package com.example.brickpaint;

import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;

/**
 * Handles methods that manipulate the position and size of nodes within javafx
 *
 * @author Fabian
 * @author matde
 * @see <a href="https://stackoverflow.com/a/38719541">Source Code</a>
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
     * handles animating/scaling a node within a 2d axis based on mouse input
     *
     * @param node   The node to zoom in/out
     * @param zoomFactor Scaling value that determines how much the node is scaled with each call
     * @param x      X cord from which to zoom about (Optional)
     * @param y      Y cord from which to zoom about (Optional)
     */
    public void zoom(Group group, Node node, double zoomFactor, double x, double y, ScrollPane scrollPane) {
        if (!Keys.activeKeys.getActiveKeys().contains(KeyCode.CONTROL)) {
            return;
        }

        Bounds groupBounds = group.getLayoutBounds();
        final Bounds viewportBounds = scrollPane.getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = scrollPane.getHvalue() * (groupBounds.getWidth() - viewportBounds.getWidth());
        double valY = scrollPane.getVvalue() * (groupBounds.getHeight() - viewportBounds.getHeight());

        // convert content coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = node.parentToLocal(group.parentToLocal(new Point2D(x, y)));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = node.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // do the resizing
        node.setScaleX(zoomFactor * node.getScaleX());
        node.setScaleY(zoomFactor * node.getScaleY());

        // refresh ScrollPane scroll positions & content bounds
        scrollPane.layout();

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        groupBounds = group.getLayoutBounds();
        scrollPane.setHvalue((valX + adjustment.getX()) * zoomFactor / (groupBounds.getWidth() - viewportBounds.getWidth()));
        scrollPane.setVvalue((valY + adjustment.getY()) * zoomFactor / (groupBounds.getHeight() - viewportBounds.getHeight()));
    }
}