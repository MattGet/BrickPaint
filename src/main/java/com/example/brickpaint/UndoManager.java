package com.example.brickpaint;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Stack;


/**
 * Handles undo and redo implementation for a JavaFX canvas
 *
 * @author matde
 */
public class UndoManager {

    /**
     * The stack of logged actions available to undo
     */
    private final Stack<Image> history = new Stack<Image>();

    /**
     * The stack of actions that were undone
     */
    private final Stack<Image> trash = new Stack<Image>();

    /**
     * A position in the history stack to track in order to later go back to
     */
    private int Mark;

    /**
     * Sets the value of Mark
     */
    public void setMark() {
        Mark = history.size();
    }

    /**
     * Will reset the provided canvas to the point in history denoted by the mark value
     *
     * @param panel The canvas to preform this action on
     */
    public void mergeToMark(CanvasPanel panel) {
        int curr = history.size() - Mark;
        for (int i = 0; i < curr - 1; i++) {
            Undo(panel);
        }
    }

    /**
     * Push the current canvas to history as an image, if the stack is larger than 100 items remove the oldest item
     *
     * @param panel The canvas to take a snapshot of
     */
    public void LogU(CanvasPanel panel) {
        Image image = getUnScaledImage(panel.canvas);
        history.push(image);
        if (history.size() >= 100) {
            history.remove(0);
            System.out.println("removed item");
        }
        System.out.println("Called LogU");
        trash.clear();
    }

    /**
     * Push the current canvas to trash as an image, if the stack is larger than 100 items remove the oldest item
     *
     * @param panel The canvas to take a snapshot of
     */
    private void LogR(CanvasPanel panel) {
        Image image = getUnScaledImage(panel.canvas);
        trash.push(image);
        if (trash.size() >= 100) {
            trash.remove(0);
            System.out.println("removed item");
        }
        System.out.println("Called LogR");
    }

    /**
     * Resets the provided canvas to the last logged state
     *
     * @param panel The canvas to write the logged history to
     */
    public void Undo(CanvasPanel panel) {
        if (!this.history.empty()){
            LogR(panel);
            panel.canvas.getGraphicsContext2D().setEffect(null);
            panel.canvas.getGraphicsContext2D().clearRect(0, 0, panel.canvas.getWidth(), panel.canvas.getHeight());
            Image content;
            if (history.size() == 1){
                content = history.peek();
            }
            else{
                content = history.pop();
            }

            double y = panel.root.getScaleY();
            double x = panel.root.getScaleX();
            panel.root.setScaleY(1);
            panel.root.setScaleX(1);
            panel.setSizeY(content.getHeight());
            panel.setSizeX(content.getWidth());
            panel.canvas.getGraphicsContext2D().drawImage(content, 0, 0);
            panel.root.setScaleX(x);
            panel.root.setScaleY(y);
            System.out.println("Called Undo");
        }
        else{
            System.err.println("History stack was empty");
        }

    }

    /**
     * Re-write the last undo action to the provided canvas
     *
     * @param panel The canvas to re-write the logged undo to
     */
    public void Redo(CanvasPanel panel){
        if (! trash.empty()){
            panel.canvas.getGraphicsContext2D().setEffect(null);
            panel.canvas.getGraphicsContext2D().clearRect(0, 0, panel.canvas.getWidth(), panel.canvas.getHeight());
            Image content = trash.pop();
            double y = panel.root.getScaleY();
            double x = panel.root.getScaleX();
            panel.root.setScaleY(1);
            panel.root.setScaleX(1);
            panel.setSizeY(content.getHeight());
            panel.setSizeX(content.getWidth());
            panel.canvas.getGraphicsContext2D().drawImage(content, 0, 0);
            panel.root.setScaleX(x);
            panel.root.setScaleY(y);
            history.push(content);
            System.out.println("Called Redo");
        }
        else {
            System.err.println("Trash stack was empty");
        }
    }

    /**
     * Helper function to return an image of the unscaled canvas
     *
     * @param canvas the parent Node of the canvas
     * @return Unscaled javaFX image of the canvas
     */
    public static Image getUnScaledImage(Canvas canvas) {
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
