package com.example.brickpaint;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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
    public static void Paste(CanvasPanel panel, Image image, Point2D point) {
        panel.canvas.getGraphicsContext2D().drawImage(image, point.getX(), point.getY());
    }

    /**
     * Adds an image to a Canvas component in javaFX at the point (0,0)
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     */
    public static void Paste(CanvasPanel panel, Image image) {
        panel.canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    /**
     * Adds an image to a Canvas component in javaFX at a specified position and rescales the canvas if needed
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     * @param point the location of the top left of the image
     */
    public static void PasteRotate(CanvasPanel panel, Image image, Point2D point) {
        double x = image.getWidth();
        double y = image.getHeight();
        if (x > panel.canvas.getWidth()) panel.setSizeX(x);
        if (y > panel.canvas.getHeight()) panel.setSizeY(y);
        panel.UpdateSize();
        panel.canvas.getGraphicsContext2D().drawImage(image,
                BrickPaintController.clamp(point.getX() - x / 2, 0, panel.canvas.getWidth()),
                BrickPaintController.clamp(point.getY() - y / 2, 0, panel.canvas.getHeight()));
    }

    /**
     * Will render a writable image to the provided canvas
     *
     * @param image  Image to render
     * @param canvas Canvas to render the image to
     * @param sx     Top left source image x position
     * @param sy     Top left source image y position
     * @param sw     Source image width
     * @param sh     Source image height
     * @param tx     The x position to draw the image at
     * @param ty     The y position to draw the image at
     */
    public static void render(WritableImage image, Canvas canvas, int sx, int sy, int sw, int sh, int tx, int ty) {
        PixelReader reader = getScaledImage(canvas).getPixelReader();
        for (int x = 0; x < sw; x++) {
            for (int y = 0; y < sh; y++) {
                Color color = image.getPixelReader().getColor(sx + x, sy + y);
                if (color != reader.getColor(sx + x, sy + y)) {
                    canvas.getGraphicsContext2D().getPixelWriter().setColor(tx + x, ty + y, color);
                }
            }
        }
    }


    /**
     * Gets a writable image of the provided canvas's content
     *
     * @param canvas The canvas to create an image from
     * @return WritableImage of the provided canvas
     */
    public static WritableImage getScaledImage(Canvas canvas) {
        Bounds bounds = canvas.getLayoutBounds();
        double scale;
        scale = 1;
        int imageWidth = (int) Math.round(bounds.getWidth() * scale);
        int imageHeight = (int) Math.round(bounds.getHeight() * scale);
        SnapshotParameters snapPara = new SnapshotParameters();
        snapPara.setFill(Color.TRANSPARENT);
        snapPara.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
        WritableImage snapshot = new WritableImage(imageWidth, imageHeight);
        snapshot = canvas.snapshot(snapPara, snapshot);
        return snapshot;
    }
}
