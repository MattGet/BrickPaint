package com.example.brickpaint;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Optional;

public class BrickPaintController {

    private File ImageFile;

    @FXML
    private AnchorPane root;

    @FXML
    public AnchorPane canvasPanel;

    @FXML
    protected ImageView imageView;
    private String ImageURL;

    @FXML
    private MenuItem closeButton;

    @FXML
    private MenuItem insertImage;

    private BrickKeys keyBinds;

    protected void Start(){
        canvasPanel.setOnScroll(this::onScroll);
        canvasPanel.setOnMouseDragged(this::onDrag);
        Scene scene = root.getScene();
        keyBinds = new BrickKeys(scene, this);
        keyBinds.SetKeyBinds();
    }

    @FXML
    protected void handleButtonClose(){
        root.getScene().getWindow().fireEvent(new WindowEvent(root.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    protected void handleInsertImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg"));
        File imageFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        Image tempImage = new Image(imageFile.toURI().toString());
        ImageURL = imageFile.toURI().toString();
        System.out.print(ImageURL);
        BrickImage.Insert(imageView, tempImage);
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

    protected void OnClose(WindowEvent event){
        if (ImageFile == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            ButtonType save = new ButtonType("SAVE & QUIT");
            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(save);
            alert.setHeaderText("You Have Not Saved This Project Recently!");
            alert.setTitle("Quit application");
            alert.setContentText(String.format("Close without saving?"));
            alert.initOwner(root.getScene().getWindow());
            Optional<ButtonType> res = alert.showAndWait();
            if(res.isPresent()) {
                if(res.get().equals(ButtonType.CANCEL))
                    event.consume();
                if(res.get().equals(ButtonType.YES)){
                    Platform.exit();
                }
                if (res.get().equals(save)){
                    ImageFile = BrickSave.saveImageASFromNode(canvasPanel, root, ImageURL);
                    Platform.exit();
                }
            }
        }
    }

    public void onDrag(MouseEvent event){
        double zoomFactor = 1.5;
        if (event.getY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }
        AnimatedZoomOperator operator = new AnimatedZoomOperator();
        operator.pan(canvasPanel, zoomFactor, event.getSceneX(), event.getSceneY());
    }

    public void onScroll (ScrollEvent event){
        double zoomFactor = 1.5;
        if (event.getDeltaY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }
        AnimatedZoomOperator operator = new AnimatedZoomOperator();
        operator.zoom(canvasPanel, zoomFactor, event.getSceneX(), event.getSceneY());

        /*double ScrollModifier = 1000;
        double modifierY = canvasPanel.getScaleY() + event.getDeltaY()/ScrollModifier;
        System.out.println("Y = " + modifierY);
            canvasPanel.setScaleX(clamp(modifierY, 0.1, 1));
            canvasPanel.setScaleY(clamp(modifierY, 0.1, 1));

         */
    }




}