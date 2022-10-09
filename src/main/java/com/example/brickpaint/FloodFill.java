package com.example.brickpaint;

import javafx.application.Platform;
import javafx.scene.image.Image;
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

    private final PixelWriter writer;

    public FloodFill(WritableImage Image, int x2, int y2, Color newColor, Color replace, double Sensitivity, CanvasPanel panelIn) {
        colorToReplace = replace;
        NewColor = newColor;
        image = Image;
        start = new Point(x2, y2);
        panel = panelIn;
        sensitivity = Sensitivity;
        writer = Image.getPixelWriter();
    }

    private static boolean compareColor(int x, int y) {
        Color color = image.getPixelReader().getColor(x, y);
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

        if (NewColor.equals(colorToReplace)) {
            System.out.println("Finished Flood Fill");
            return null;
        }

        ArrayDeque<int[]> stack = new ArrayDeque<int[]>((int) (image.getWidth() * image.getHeight()));

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

        Platform.runLater(() -> {
            System.out.println("Finished Flood Fill");
            Image result = image;
            panel.canvas.getGraphicsContext2D().clearRect(0,0, panel.canvas.getWidth(), panel.canvas.getHeight());
            panel.canvas.getGraphicsContext2D().drawImage(result, 0, 0, image.getWidth(), image.getHeight(), 0, 0, panel.canvas.getWidth(), panel.canvas.getWidth());
        });
        return null;
    }

    private void push(ArrayDeque<int[]> stack, int x, int y) {
        if (x <= 0 || x >= image.getWidth() ||
                y <= 0 || y >= image.getHeight()) {
            return;
        }
        else if (!compareColor(x, y)) {
            return;
        }
        else{
            writer.setColor(x, y, NewColor);
        }
        stack.push(new int[]{x, y});
    }

}
