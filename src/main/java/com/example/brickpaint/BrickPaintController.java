package com.example.brickpaint;

import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
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
import java.util.*;


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
    public ComboBox<Double> lineWidth;

    /**
     * allows the user to enter or select a value for the current canvas's width
     */
    @FXML
    public ComboBox<Double> cWidth;
    /**
     * allows the user to enter or select a value for the current canvas's height
     */
    @FXML
    public ComboBox<Double> cHeight;
    /**
     * allows the user to select their desired line style from a dropdown menu
     */
    @FXML
    protected ChoiceBox<BrickTools> lineType;
    /**
     * A dictionary of the files used to save the canvasPanel, created after the first SaveAs is called
     */
    private final HashMap<Integer, File> ImageFile = new HashMap<>();
    /**
     * The uppermost Node within the current scene
     */
    @FXML
    private AnchorPane root;
    /**
     * The instance of the BrickKeys class that manages keybinds for this controller
     */
    private BrickKeys keyBinds;
    /**
     * The node that manages all of the canvas panel tabs within the application
     */
    @FXML
    private TabPane tabs;
    /**
     * A group of buttons representing available tools in the application, restricted so that only one may be pressed
     * at a time
     */
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
        lineWidth.getItems().addAll(1d, 2d, 4d, 8d, 10d, 12d, 14d, 18d, 24d, 30d, 36d, 48d, 60d, 72d);
        lineWidth.setValue(10d);
        lineType.getItems().addAll(BrickTools.SolidLine, BrickTools.DashedLine);
        lineType.setValue(BrickTools.SolidLine);
        cWidth.getItems().addAll(256d, 512d, 720d, 1024d, 1080d, 1280d, 1440d, 1920d);
        cHeight.getItems().addAll(256d, 512d, 720d, 1024d, 1080d, 1280d, 1440d, 1920d);
        colorPicker.setValue(Color.BLACK);
        cGroup.getToggles().get(0).setSelected(true);
        cGroup.getToggles().get(0).setGraphic(new Icon());
    }

    /**
     * Creates a new canvas panel instance and adds it to the scene as a tab
     */
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

    /**
     * Returns the current canvas panel that is selected in the Tab pane
     *
     * @return CanvasPanel
     */
    public CanvasPanel getCanvas() {
        CanvasPanel panel;
        int curr = tabs.getSelectionModel().getSelectedIndex();
        panel = canvasPanels.get(curr);
        return panel;
    }


    /**
     * Returns the current tool selection from the tooltype toggle group as a BrickTools enum value
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
                    return BrickTools.RainbowPencil;
                } else if (cGroup.getToggles().indexOf(button) == 3) {
                    return BrickTools.Line;
                } else if (cGroup.getToggles().indexOf(button) == 4) {
                    return BrickTools.Rectangle;
                } else if (cGroup.getToggles().indexOf(button) == 5) {
                    return BrickTools.Square;
                } else if (cGroup.getToggles().indexOf(button) == 6) {
                    return BrickTools.Circle;
                } else if (cGroup.getToggles().indexOf(button) == 7) {
                    return BrickTools.Oval;
                } else if (cGroup.getToggles().indexOf(button) == 8) {
                    return BrickTools.ColorGrabber;
                }
            }
        }
        return BrickTools.Pointer;
    }

    /**
     * Sets the current selected tool to the standard mouse pointer
     */
    public void resetGrabber() {
        cGroup.getToggles().get(0).setSelected(true);
    }

    /**
     * Changes the mouse cursor when the grabber tool is selected
     */
    @FXML
    private void handleGrabber() {
        Image temp = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/dropper.png")));
        ImageCursor cursor = new ImageCursor(temp, 0, temp.getHeight());
        root.getScene().setCursor(cursor);
    }

    /**
     * Updates the current canvas's width when the user changes its value
     */
    @FXML
    protected void handleCWidth() {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            this.getCanvas().setSizeX(numb);
            //System.out.println("Set Width");
        } catch (Exception e) {
            System.out.println("Canvas Width input was Invalid");
        }
    }

    /**
     * Updates the current canvas's height when the user changes its value
     */
    @FXML
    protected void handleCHeight() {
        try {
            String value = cHeight.getEditor().getText();
            double numb = Double.parseDouble(value);
            this.getCanvas().setSizeY(numb);
            //System.out.println("Set Height");
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
     * Resets the scale and position of the current canvas
     */
    @FXML
    protected void handleResetView() {
        keyBinds.reset();
    }

    /**
     * Creates a prompt to confirm clearing the current canvas
     */
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
        if (ImageFile.get(tabs.getSelectionModel().getSelectedIndex()) == null) {
            ImageFile.putIfAbsent(tabs.getSelectionModel().getSelectedIndex(), BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name));
            return;
        }
        BrickSave.saveImageFromNode(this.getCanvas().root, ImageFile.get(tabs.getSelectionModel().getSelectedIndex()));
    }

    /**
     * Handles saving an image to a user created file from the canvasPanel
     */
    @FXML
    protected void handleSaveImageAs() {
        ImageFile.putIfAbsent(tabs.getSelectionModel().getSelectedIndex(), BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name));
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
        boolean needSave = ImageFile.size() != tabs.getTabs().size();
        if (needSave) {
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
                    for (int i = 0; i <= tabs.getTabs().size() - 1; i++) {
                        if (ImageFile.containsKey(i)) continue;
                        else {
                            ImageFile.putIfAbsent(i, BrickSave.saveImageASFromNode(canvasPanels.get(i).root, canvasPanels.get(i).Name));
                        }
                    }
                    Platform.exit();
                }
            }
        } else {
            Platform.exit();
        }
    }

}