package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles saving a snapshot image of a JavaFX Node with user created or predefined files
 *
 * @author matde
 */
public abstract class BrickSave {

    /**
     * Takes a snapshot of a Node and saves it to the specified file
     *
     * @param node The Node from which to take a screenshot of
     * @param file The File to save the image to
     */
    public static void saveImageFromNode(Node node, File file) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageToSave = node.snapshot(parameters, null);
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a snapshot of a Node and saves it to a file created by the user. Will open the file explorer
     * and initially name the file based off the ImagePath
     *
     * @param node      The node from which to take a screenshot of
     * @param ImagePath Optional path string from which to initially name the new file with
     * @return returns file that image was saved to, else returns null
     */
    public static File saveImageASFromNode(Node node, String ImagePath) {
        if (ImagePath == null) {
            ImagePath = "";
        }
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageToSave = node.snapshot(parameters, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        int StartIndex = ImagePath.lastIndexOf('/');
        String fileName = ImagePath.substring(StartIndex + 1);
        fileName = fileName.replace('%', ' ');
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        //try to open file chooser and save the specified image
        try {
            File file = fileChooser.showSaveDialog(node.getScene().getWindow());
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", file);
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
            return file;
        }
        //else return null as no file was saved
        catch (IOException e) {
            return null;
        }
    }
}
