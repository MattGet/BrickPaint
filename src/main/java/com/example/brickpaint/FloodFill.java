package com.example.brickpaint;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class FloodFill implements Callable<WritableImage> {

    private final Color colorToReplace;
    private final Color NewColor;
    private final double sensitivity;
    private final Point start;
    private final WritableImage image;
    private final PixelWriter writer;

    public FloodFill( WritableImage imageIn, int x2, int y2, Color replace, Color newColor, double Sense) {
        colorToReplace = replace;
        NewColor = newColor;
        sensitivity = Sense;
        start = new Point(x2, y2);
        image = imageIn;
        writer = image.getPixelWriter();
    }

    private boolean compareColor(int x, int y) {
        Color color = image.getPixelReader().getColor(x, y);
        return (withinTolerance(color, colorToReplace, sensitivity));
    }

    private boolean withinTolerance(Color a, Color b, double epsilon) {
        return
                withinTolerance(a.getRed(), b.getRed(), epsilon) &&
                        withinTolerance(a.getGreen(), b.getGreen(), epsilon) &&
                        withinTolerance(a.getBlue(), b.getBlue(), epsilon) &&
                        withinTolerance(a.getOpacity(), b.getOpacity(), epsilon);
    }

    private boolean withinTolerance(double a, double b, double epsilon) {
        return Math.abs(a - b) <= epsilon;
    }

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
            }
            catch (Exception e){
                System.out.println("Ran out of memory when running flood fill, stopping execution");
                return null;
            }
        }
        return image;
    }


    private void push(ArrayDeque<int[]> stack, int x, int y) {
        if (x <= 0 || x >= image.getWidth() ||
                y <= 0 || y >= image.getHeight()) {
            return;
        }
        else if (!compareColor(x, y)) {
            writer.setColor(x, y, NewColor);
            return;
        }
        else{
            writer.setColor(x, y, NewColor);
        }
        stack.push(new int[]{x, y});
    }

}
