package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

/**
 * Handles common image functions for javaFX applications
 * @author matde
 */
public abstract class BrickImage {

    /**
     * Adds an image to an imageView component in javaFX, scales imageView to the size of input image
     * @param image
     * @param panel
     */
    public static void Insert(CanvasPanel panel, ImageView view, Image image){
        double x = image.getWidth();
        double y = image.getHeight();


        panel.root.setPrefWidth(x);
        panel.root.setPrefHeight(y);
        view.setFitWidth(x);
        view.setFitHeight(y);
        panel.root.getChildren().add(view);
    }
}
