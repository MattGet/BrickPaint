package com.example.brickpaint;

import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;

public class FloodFill implements Callable<Object> {

    private static Color colorToReplace;
    private static Color NewColor;
    private static double sensitivity;
    private static WritableImage image;
    private final Point start;
    private final CanvasPanel panel;

    public FloodFill(WritableImage Image, int x2, int y2, Color newColor, Color replace, double Sensitivity, CanvasPanel panelIn) {
        colorToReplace = replace;
        NewColor = newColor;
        image = Image;
        start = new Point(x2, y2);
        panel = panelIn;
        sensitivity = Sensitivity;
    }

    private static boolean compareColor(int[] point) {
        Color color = image.getPixelReader().getColor(point[0], point[1]);
        return (withinTolerance(color, colorToReplace, sensitivity));
    }

    private static boolean withinTolerance(Color a, Color b, double epsilon) {
        return
                withinTolerance(a.getRed(), b.getRed(), epsilon) &&
                        withinTolerance(a.getGreen(), b.getGreen(), epsilon) &&
                        withinTolerance(a.getBlue(), b.getBlue(), epsilon) &&
                        withinTolerance(a.getOpacity(), b.getOpacity(), epsilon);
    }

    private static boolean withinTolerance(double a, double b, double epsilon) {
        return Math.abs(a - b) <= epsilon;
    }

    @Override
    public Object call() {
        PixelWriter writer = image.getPixelWriter();

        if (NewColor.equals(colorToReplace)) {
            System.out.println("Finished Flood Fill");
            return null;
        }

        ArrayDeque<int[]> stack = new ArrayDeque<int[]>((int) (image.getWidth() * image.getHeight()));

        stack.add(new int[]{start.x, start.y});

        while (!stack.isEmpty()) {
            int[] point = stack.pop();
            if (!compareColor(point)) {
                continue;
            }
            int x = point[0];
            int y = point[1];


            writer.setColor(point[0], point[1], NewColor);

            try {
                //push(stack, x - 1, y - 1);
                push(stack, x - 1, y);
                //push(stack, x - 1, y + 1);
                push(stack, x, y + 1);
                //push(stack, x + 1, y + 1);
                push(stack, x + 1, y);
                //push(stack, x + 1, y - 1);
                push(stack, x, y - 1);
            }
            catch (Exception e){
                System.out.println("Ran out of memory when running flood fill, stopping execution");
                return null;
            }
        }

        Platform.runLater(() -> {
            System.out.println("Finished Flood Fill");
            BrickImage.Paste(panel, image);
        });
        return null;
    }

    private void push(ArrayDeque<int[]> stack, int x, int y) {
        if (x <= 0 || x >= image.getWidth() ||
                y <= 0 || y >= image.getHeight()) {
            return;
        }
        stack.push(new int[]{x, y});
    }

}
