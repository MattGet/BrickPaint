package com.example.brickpaint;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.File;

public class BrickPaintController {

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
    protected void handleSaveImageAs(){
        WritableImage imageToSave = canvasPanel.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        int StartIndex = ImageURL.lastIndexOf('/');
        String fileName = ImageURL.substring(StartIndex);
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png"));
        try{
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    protected void OnClose(){
        Platform.exit();
    }

}