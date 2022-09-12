package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.scene.robot.Robot;

/**
 * Handles common image functions for javaFX applications
 * @author matde
 */
public abstract class BrickImage {

    /**
     * Adds an image to an imageView component in javaFX, scales imageView to the size of input image
     * @param robot
     * @param image
     * @param panel
     */
    public static void Insert(CanvasPanel panel, Robot robot, Image image){
        double x = image.getWidth();
        double y = image.getHeight();
        //panel.root.setPrefWidth(x);
        //panel.root.setPrefHeight(y);
        panel.canvas.setWidth(x);
        panel.canvas.setHeight(y);

        if (image != null){
            panel.canvas.getGraphicsContext2D().drawImage(image, 0, 0);
        }
        else {System.out.println("image was null");}

    }
}
