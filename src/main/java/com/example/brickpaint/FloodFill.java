package com.example.brickpaint;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;

/**
 * Handles running a threaded flood fill algorithm
 *
 * @author matde
 */
public class FloodFill implements Callable<WritableImage> {

    private final Color colorToReplace;
    private final Color NewColor;
    private final double sensitivity;
    private final Point start;
    private final WritableImage image;
    private final PixelWriter writer;

    /**
     * Defualt constructor
     *
     * @param imageIn  the image to preform the flood fill on
     * @param x2       start x-cord
     * @param y2       start y-cord
     * @param replace  color to replace
     * @param newColor color to draw with
     * @param Sense    the sensitivity for determining the border
     */
    public FloodFill(WritableImage imageIn, int x2, int y2, Color replace, Color newColor, double Sense) {
        colorToReplace = replace;
        NewColor = newColor;
        sensitivity = Sense;
        start = new Point(x2, y2);
        image = imageIn;
        writer = image.getPixelWriter();
    }

    /**
     * Helper function which returns true if the color at location (x,y) matches the color to replace within
     * a certain tolerance
     *
     * @param x horizontal position
     * @param y vertical position
     * @return boolean true or false
     */
    private boolean compareColor(int x, int y) {
        Color color = image.getPixelReader().getColor(x, y);
        return (withinTolerance(color, colorToReplace, sensitivity));
    }


    /**
     * Helper function that compares two colors within a given tolerance
     *
     * @param a       first color
     * @param b       second color
     * @param epsilon tolerance value
     * @return boolean true or false
     */
    private boolean withinTolerance(Color a, Color b, double epsilon) {
        return
                withinTolerance(a.getRed(), b.getRed(), epsilon) &&
                        withinTolerance(a.getGreen(), b.getGreen(), epsilon) &&
                        withinTolerance(a.getBlue(), b.getBlue(), epsilon) &&
                        withinTolerance(a.getOpacity(), b.getOpacity(), epsilon);
    }


    /**
     * @hidden
     */
    private boolean withinTolerance(double a, double b, double epsilon) {
        return Math.abs(a - b) <= epsilon;
    }

    /**
     * Threaded flood fill algorithm
     *
     * @return WritableImage with the flood fill operation applied to it
     */
    @Override
    public WritableImage call() {
        if (NewColor.equals(colorToReplace)) {
            System.out.println("Finished Flood Fill, colors matched");
            return null;
        }

        ArrayDeque<int[]> stack = new ArrayDeque<int[]>((int) Math.ceil((image.getHeight() * image.getWidth())));

        stack.add(new int[]{start.x, start.y});

        while (!stack.isEmpty()) {
            int[] point = stack.pop();
            int x = point[0];
            int y = point[1];

            try {
                push(stack, x - 1, y - 1);
                push(stack, x - 1, y);
                push(stack, x - 1, y + 1);
                push(stack, x, y + 1);
                push(stack, x + 1, y + 1);
                push(stack, x + 1, y);
                push(stack, x + 1, y - 1);
                push(stack, x, y - 1);
            } catch (Exception e) {
                System.out.println("Ran out of memory when running flood fill, stopping execution");
                return null;
            }
        }
        return image;
    }


    /**
     * Helper function which determins if a point should be pushed onto the stack or not,
     * if it is then the point gets recolored as well
     *
     * @param stack The stack which the point should be pushed to
     * @param x     The x position of the point
     * @param y     The y position of the point
     */
    private void push(ArrayDeque<int[]> stack, int x, int y) {
        if (x <= 0 || x >= image.getWidth() ||
                y <= 0 || y >= image.getHeight()) {
            return;
        } else if (!compareColor(x, y)) {
            writer.setColor(x, y, NewColor);
            return;
        } else {
            writer.setColor(x, y, NewColor);
        }
        stack.push(new int[]{x, y});
    }

}
