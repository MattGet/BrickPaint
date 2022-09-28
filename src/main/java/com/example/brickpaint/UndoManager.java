package com.example.brickpaint;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Stack;

public class UndoManager {
    private final Stack<Image> history = new Stack<>();
    private final Stack<Image> trash = new Stack<>();
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
        LogU(panel);
    }

    public void LogU(CanvasPanel panel) {
        Image image = getUnScaledImage(panel.root);
        history.push(image);
        if (history.size() >= 100) {
            history.remove(0);
            System.out.println("removed item");
        }
        System.out.println("Called LogU");
    }

    public void LogR(CanvasPanel panel) {
        Image image = getUnScaledImage(panel.root);
        trash.push(image);
        if (trash.size() >= 100) {
            trash.remove(0);
            System.out.println("removed item");
        }
        System.out.println("Called LogR");
    }

    public void Undo(CanvasPanel panel) {
        panel.canvas.getGraphicsContext2D().clearRect(0, 0, panel.canvas.getWidth(), panel.canvas.getHeight());
        if (history.size() == 0) {
            return;
        }
        Image content = history.pop();
        double y = panel.root.getScaleY();
        double x = panel.root.getScaleX();
        panel.root.setScaleY(1);
        panel.root.setScaleX(1);
        panel.canvas.getGraphicsContext2D().drawImage(content, 0, 0);
        panel.root.setScaleX(x);
        panel.root.setScaleY(y);
        trash.push(content);
        System.out.println("Called Undo");
    }

    public void Redo(CanvasPanel panel){
        if (trash.size() == 0) {
            return;
        }
        panel.canvas.getGraphicsContext2D().clearRect(0, 0, panel.canvas.getWidth(), panel.canvas.getHeight());
        Image content = trash.pop();
        double y = panel.root.getScaleY();
        double x = panel.root.getScaleX();
        panel.root.setScaleY(1);
        panel.root.setScaleX(1);
        panel.canvas.getGraphicsContext2D().drawImage(content, 0, 0);
        panel.root.setScaleX(x);
        panel.root.setScaleY(y);
        history.push(content);
        System.out.println("Called Redo");
    }

    public Image getUnScaledImage(StackPane canvas) {
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
