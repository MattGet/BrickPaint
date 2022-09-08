package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

/**
 * Handles common image functions for javaFX applications
 * @author matde
 */
public abstract class BrickImage {

    /**
     * Adds an image to an imageView component in javaFX, scales imageView to the size of input image
     * @param imageView
     * @param image
     */
    public static void Insert(ImageView imageView, Image image){
        double x = image.getWidth();
        double y = image.getHeight();

        imageView.setFitHeight(y);
        imageView.setFitWidth(x);
        imageView.setImage(image);
    }
}
