package com.example.brickpaint;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;

public class BrickPaintController {

    private File ImageFile;

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane canvasPanel;

    @FXML
    protected ImageView imageView;
    private String ImageURL;

    @FXML
    private MenuItem closeButton;

    @FXML
    private MenuItem insertImage;

    @FXML
    protected void handleButtonClose(){
        OnClose();
    }

    @FXML
    protected void handleInsertImage(){

        File imageFile = new FileChooser().showOpenDialog(root.getScene().getWindow());
        Image tempImage = new Image(imageFile.toURI().toString());
        ImageURL = imageFile.toURI().toString();
        System.out.print(ImageURL);
        imageView.setImage(tempImage);
    }

    @FXML
    protected void handleSaveImage(){
        if (ImageFile == null) {
            this.handleSaveImageAs();
            return;
        }
        WritableImage imageToSave = canvasPanel.snapshot(new SnapshotParameters(), null);
        try{
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", ImageFile);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSaveImageAs(){
        WritableImage imageToSave = canvasPanel.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        int StartIndex = ImageURL.lastIndexOf('/');
        String fileName = ImageURL.substring(StartIndex + 1);
        fileName = fileName.replace('%', ' ');
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png"));
        try{
            ImageFile = fileChooser.showSaveDialog(root.getScene().getWindow());
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            ImageIO.write(bImage, "png", ImageFile);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void OnClose(){
        Platform.exit();
    }

}