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


/**
 * Main controller class for the BrickPaint application, handles the FXML methods and variables for the stage
 * @author matde
 */
public class BrickPaintController {

    /**
     * The file used to save the canvasPanel, created after the first SaveAs is called
     */
    private File ImageFile;

    /**
     * The uppermost Node within the current scene
     */
    @FXML
    private AnchorPane root;

    @FXML
    private  AnchorPane middleRoot;

    public CanvasPanel canvasPanel;

    /**
     * The path of the source image inserted by the user
     */
    private String ImageURL;

    @FXML
    private MenuItem closeButton;

    @FXML
    private MenuItem insertImage;

    /**
     * The instance of the BrickKeys class that manages keybinds for this controller
     */
    private BrickKeys keyBinds;

    /**
     * Called when the program starts from the application class
     */
    protected void Start(){
        canvasPanel = new CanvasPanel(middleRoot);
        Scene scene = root.getScene();
        keyBinds = new BrickKeys(scene, this);
        keyBinds.SetKeyBinds();
        canvasPanel.Setup(keyBinds);
    }

    /**
     * Handles the action when a close button is pressed
     */
    @FXML
    protected void handleButtonClose(){
        root.getScene().getWindow().fireEvent(new WindowEvent(root.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Handles the action when the reset view button is pressed, manually invokes the function in BrickKeys
     */
    @FXML
    protected void handleResetView(){
        keyBinds.reset();
    }

    /**
     * Handles inserting an image into the imageVeiw component, utilizes the file explorer and BrickImage class
     */
    @FXML
    protected void handleInsertImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg"));
        File imageFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        Image tempImage = new Image(imageFile.toURI().toString());
        ImageURL = imageFile.toURI().toString();
        System.out.print(ImageURL);
        BrickImage.Insert(canvasPanel, canvasPanel.imageView, tempImage);
    }

    /**
     * Handles saving an image from the canvasPanel, if it has not yet been saved will call SaveAs instead
     */
    @FXML
    protected void handleSaveImage(){
        if (ImageFile == null) {
            ImageFile = BrickSave.saveImageASFromNode(canvasPanel.root, root, ImageURL);
            return;
        }
        BrickSave.saveImageFromNode(canvasPanel.root, ImageFile);
    }

    /**
     * Handles saving an image to a user created file from the canvasPanel
     */
    @FXML
    protected void handleSaveImageAs(){
      ImageFile = BrickSave.saveImageASFromNode(canvasPanel.root, root, ImageURL);
    }

    /**
     * This method is called or invoked whenever the program is going to be closed
     * @param event
     */
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
                    ImageFile = BrickSave.saveImageASFromNode(canvasPanel.root, root, ImageURL);
                    Platform.exit();
                }
            }
        }
    }






}