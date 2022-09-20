package com.example.brickpaint;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Stack;

public class UndoManager {
    private final Stack<Image> history = new Stack<Image>();
    private int Mark;

    public void setMark() {
        Mark = history.size();
    }

    public void mergeToMark(CanvasPanel panel) {
        int curr = history.size() - Mark;
        Image temp = history.pop();
        for (int i = 0; i < curr - 1; i++) {
            Undo(panel);
        }
        panel.canvas.getGraphicsContext2D().clearRect(0, 0, panel.canvas.getWidth(), panel.canvas.getHeight());
        panel.canvas.getGraphicsContext2D().drawImage(temp, 0, 0);
        Log(panel);
    }

    public void Log(CanvasPanel panel) {
        Image image = getUnScaledImage(panel.canvas);
        history.push(image);
        if (history.size() >= 100) {
            history.remove(0);
            System.out.println("removed item");
        }
        System.out.println("Called Log");
    }

    public void Undo(CanvasPanel panel) {
        panel.canvas.getGraphicsContext2D().clearRect(0, 0, 2000, 2000);
        if (history.size() == 0) {
            return;
        }
        System.out.println(history.size());
        double y = panel.canvas.getScaleY();
        double x = panel.canvas.getScaleX();
        panel.canvas.setScaleY(1);
        panel.canvas.setScaleX(1);
        panel.canvas.getGraphicsContext2D().drawImage(history.pop(), 0, 0);
        panel.canvas.setScaleX(x);
        panel.canvas.setScaleY(y);
        System.out.println("Called Undo");
    }

    public Image getUnScaledImage(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        double y = canvas.getScaleY();
        double x = canvas.getScaleX();
        canvas.setScaleY(1);
        canvas.setScaleX(1);
        Image image = canvas.snapshot(parameters, null);
        canvas.setScaleX(x);
        canvas.setScaleY(y);
        return image;
    }

}
