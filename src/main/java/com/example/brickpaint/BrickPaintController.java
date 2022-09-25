package com.example.brickpaint;

import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Main controller class for the BrickPaint application, handles the FXML methods and variables for the stage
 *
 * @author matde
 */
public class BrickPaintController {

    /**
     * The current instance of the canvas where all drawing occurs within the paint app
     */
    public List<CanvasPanel> canvasPanels = new ArrayList<>();

    /**
     * allows the user to select the current color from a colorpicker menu
     */
    @FXML
    public ColorPicker colorPicker;
    /**
     * allows the user to select the current line width from a dropdown menu
     */
    @FXML
    public ComboBox lineWidth;
    @FXML
    public TextField cWidth;
    @FXML
    public TextField cHeight;
    @FXML
    protected ChoiceBox lineType;
    /**
     * The file used to save the canvasPanel, created after the first SaveAs is called
     */
    private File ImageFile;
    /**
     * The uppermost Node within the current scene
     */
    @FXML
    private AnchorPane root;
    /**
     * The instance of the BrickKeys class that manages keybinds for this controller
     */
    private BrickKeys keyBinds;
    @FXML
    private TabPane tabs;
    @FXML
    private ToggleButtonGroup cGroup;

    /**
     * Called when the program starts from the application class
     */
    protected void Start() {
        Scene scene = root.getScene();
        keyBinds = new BrickKeys(scene, this);
        keyBinds.SetKeyBinds();
        addTab();
        lineWidth.getItems().addAll(1, 2, 4, 8, 10, 12, 14, 18, 24, 30, 36, 48, 60, 72);
        lineWidth.setValue(10);
        lineType.getItems().addAll(BrickTools.SolidLine, BrickTools.DashedLine);
        lineType.setValue(BrickTools.SolidLine);
        colorPicker.setValue(Color.BLACK);
        cGroup.getToggles().get(0).setSelected(true);
        cGroup.getToggles().get(0).setGraphic(new Icon());
    }

    @FXML
    protected void addTab() {
        TextInputDialog dialog = new TextInputDialog("Untitled " + "(" + tabs.getTabs().size() + ")");
        dialog.setHeaderText("Create New Project");
        dialog.setContentText("Enter Project Name");
        Optional<String> response = dialog.showAndWait();
        if (response.isPresent()) {
            String name = response.get();
            canvasPanels.add(new CanvasPanel(tabs, name, keyBinds, this));
        }
    }

    public CanvasPanel getCanvas() {
        CanvasPanel panel;
        int curr = tabs.getSelectionModel().getSelectedIndex();
        panel = canvasPanels.get(curr);
        return panel;
    }


    /**
     * returns the current tool selection from the tooltype toggle group as an integer
     *
     * @return int
     */
    public BrickTools getToolType() {
        for (ToggleButton button : cGroup.getToggles()) {
            if (button.isSelected()) {
                if (cGroup.getToggles().indexOf(button) == 0) {
                    return BrickTools.Pointer;
                } else if (cGroup.getToggles().indexOf(button) == 1) {
                    return BrickTools.Pencil;
                } else if (cGroup.getToggles().indexOf(button) == 2) {
                    return BrickTools.Line;
                } else if (cGroup.getToggles().indexOf(button) == 3) {
                    return BrickTools.Rectangle;
                } else if (cGroup.getToggles().indexOf(button) == 4) {
                    return BrickTools.Square;
                } else if (cGroup.getToggles().indexOf(button) == 5) {
                    return BrickTools.Circle;
                } else if (cGroup.getToggles().indexOf(button) == 6) {
                    return BrickTools.Oval;
                }
            }
        }
        return BrickTools.Pointer;
    }

    @FXML
    protected void handleCWidth() {
        try {
            String value = cWidth.getText();
            double numb = Double.parseDouble(value);
            this.getCanvas().setSizeX(numb);
            System.out.println("Set Width");
        } catch (Exception e) {
            System.out.println("Canvas Width input was Invalid");
        }
    }

    @FXML
    protected void handleCHeight() {
        try {
            String value = cHeight.getText();
            double numb = Double.parseDouble(value);
            this.getCanvas().setSizeY(numb);
            System.out.println("Set Height");
        } catch (Exception e) {
            System.out.println("Canvas Height input was Invalid");
        }
    }

    /**
     * Handles the action when a close button is pressed
     */
    @FXML
    protected void handleButtonClose() {
        root.getScene().getWindow().fireEvent(new WindowEvent(root.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Handles the action when the reset view button is pressed, manually invokes the function in BrickKeys
     */
    @FXML
    protected void handleResetView() {
        keyBinds.reset();
    }

    @FXML
    protected void handleClear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        alert.setHeaderText("Are you sure you want to clear the current canvas?");
        alert.setTitle("Clear Canvas");
        alert.initOwner(root.getScene().getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals(ButtonType.NO))
                return;
            if (result.get().equals(ButtonType.YES)) {
                this.getCanvas().clearAll();
            }
        }
    }

    /**
     * Handles inserting an image into the imageVeiw component, utilizes the file explorer and BrickImage class
     */
    @FXML
    protected void handleInsertImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg", "*.bmp"));
        File imageFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (imageFile != null) {
            Image tempImage = new Image(imageFile.toURI().toString());
            BrickImage.Insert(this.getCanvas(), tempImage);
        }
    }

    /**
     * Handles saving an image from the canvasPanel, if it has not yet been saved will call SaveAs instead
     */
    @FXML
    protected void handleSaveImage() {
        if (ImageFile == null) {
            ImageFile = BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name);
            return;
        }
        BrickSave.saveImageFromNode(this.getCanvas().root, ImageFile);
    }

    /**
     * Handles saving an image to a user created file from the canvasPanel
     */
    @FXML
    protected void handleSaveImageAs() {
        ImageFile = BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name);
    }


    /**
     * Creates a new window based off of the About Brick FXML file and controller
     */
    @FXML
    protected void handleOpenAboutMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AboutBrick.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 600, 400));
            stage.setTitle("About");
            stage.show();
            AboutBrickController cont = loader.getController();
            cont.Setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called or invoked whenever the program is going to be closed
     *
     * @param event Window event from the Event Class
     */
    protected void OnClose(WindowEvent event) {
        if (ImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            ButtonType save = new ButtonType("SAVE & QUIT");
            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(save);
            alert.setHeaderText("You Have Not Saved This Project Recently!");
            alert.setTitle("Quit application");
            alert.setContentText("Close without saving?");
            alert.initOwner(root.getScene().getWindow());
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent()) {
                if (res.get().equals(ButtonType.CANCEL))
                    event.consume();
                if (res.get().equals(ButtonType.YES)) {
                    Platform.exit();
                }
                if (res.get().equals(save)) {
                    ImageFile = BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name);
                    Platform.exit();
                }
            }
        } else {
            Platform.exit();
        }
    }

}