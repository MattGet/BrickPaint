package com.example.brickpaint;

import javafx.geometry.Point2D;
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
        panel.setSizeX(x);
        panel.setSizeY(y);
        panel.UpdateSize();
        panel.canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    /**
     * Adds an image to a Canvas component in javaFX at a specified position
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     * @param point the location of the top left of the image
     */
    public static void Paste(CanvasPanel panel, Image image, Point2D point){
        panel.canvas.getGraphicsContext2D().drawImage(image, point.getX(), point.getY());
    }

    /**
     * Adds an image to a Canvas component in javaFX at the point (0,0)
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     */
    public static void Paste(CanvasPanel panel, Image image){
        panel.canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    /**
     * Adds an image to a Canvas component in javaFX at a specified position and rescales the canvas if needed
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     * @param point the location of the top left of the image
     */
    public static void PasteRotate(CanvasPanel panel, Image image, Point2D point){
        double x = image.getWidth();
        double y = image.getHeight();
        if (x > panel.canvas.getWidth()) panel.setSizeX(x);
        if (y > panel.canvas.getHeight()) panel.setSizeY(y);
        panel.UpdateSize();
        panel.canvas.getGraphicsContext2D().drawImage(image,
                BrickPaintController.clamp(point.getX() - x/2, 0, panel.canvas.getWidth()),
                BrickPaintController.clamp(point.getY() - y/2, 0, panel.canvas.getHeight()));
    }

}
