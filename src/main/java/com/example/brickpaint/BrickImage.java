package com.example.brickpaint;

import javafx.scene.image.Image;

/**
 * Handles common image functions for javaFX applications
 *
 * @author matde
 */
public abstract class BrickImage {

    /**
     * Adds an image to an imageView component in javaFX, scales imageView to the size of input image
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     */
    public static void Insert(CanvasPanel panel, Image image) {
        double x = image.getWidth();
        double y = image.getHeight();
        panel.root.setPrefWidth(x);
        panel.root.setPrefHeight(y);
        panel.canvas.setWidth(x);
        panel.canvas.setHeight(y);
        panel.canvas.getGraphicsContext2D().drawImage(image, 0, 0);

    }
}
