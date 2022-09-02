package com.example.brickpaint;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.*;
import javafx.stage.FileChooser;

public class BrickPaintController {

    @FXML
    private AnchorPane root;

    @FXML
    protected ImageView imageView;

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
        imageView.setImage(tempImage);
    }

    protected void OnClose(){
        Platform.exit();
    }

}