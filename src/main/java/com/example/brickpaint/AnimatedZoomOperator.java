package com.example.brickpaint;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimatedZoomOperator {

    private Timeline timeline;

    public AnimatedZoomOperator() {
        this.timeline = new Timeline(60);
    }

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

    public void pan(Node node, double factor, double x, double y){
        // determine scale
        double oldScale = node.getScaleX();
        double scale = clamp( oldScale * factor, 0.01, 1);
        double f = (scale / oldScale) - 1;

        // determine offset that we will have to move the node
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(),  node.getTranslateX() + f * dx)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), node.getTranslateY() + f * dy))
        );
        timeline.play();
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}