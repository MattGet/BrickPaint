package com.example.brickpaint;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import java.io.File;

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg"));
        File imageFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        Image tempImage = new Image(imageFile.toURI().toString());
        ImageURL = imageFile.toURI().toString();
        System.out.print(ImageURL);
        imageView.setFitHeight(tempImage.getHeight());
        imageView.setFitWidth(tempImage.getWidth());
        imageView.setImage(tempImage);
    }

    @FXML
    protected void handleSaveImage(){
        if (ImageFile == null) {
            ImageFile = BrickSave.saveImageASFromNode(canvasPanel, root, ImageURL);
            return;
        }
        BrickSave.saveImageFromNode(canvasPanel, ImageFile);
    }

    @FXML
    protected void handleSaveImageAs(){
      ImageFile = BrickSave.saveImageASFromNode(canvasPanel, root, ImageURL);
    }

    protected void OnClose(){
        Platform.exit();
    }

}