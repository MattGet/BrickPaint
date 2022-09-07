package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;


public abstract class BrickSave {

    public static void saveImageFromNode(Node node, File file){
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageToSave = node.snapshot(parameters, null);
        try{
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static File saveImageASFromNode(Node node, Node root, String ImagePath){
        if (ImagePath == null) {ImagePath = "";}
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageToSave = node.snapshot(parameters, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        int StartIndex = ImagePath.lastIndexOf('/');
        String fileName = ImagePath.substring(StartIndex + 1);
        fileName = fileName.replace('%', ' ');
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg"));
        try{
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", file);
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
            return file;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
