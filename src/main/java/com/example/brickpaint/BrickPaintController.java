package com.example.brickpaint;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * Main controller class for the BrickPaint application, handles the FXML methods and variables for the stage
 *
 * @author matde
 */
public class BrickPaintController {

    //-Dlog4j.configurationFile="src/main/resources/com/example/brickpaint/log4j2.xml"
    static {
        System.setProperty("log4j.configurationFile", "src/main/resources/com/example/brickpaint/log4j2.xml");
    }

    public static final Logger logger = LogManager.getLogger(BrickPaintController.class);


    /**
     * A dictionary of the files used to save the canvasPanel, created after the first SaveAs is called
     */
    private final HashMap<Integer, File> ImageFile = new HashMap<>();
    /**
     * The current instance of the canvas where all drawing occurs within the paint app
     */
    public List<CanvasPanel> canvasPanels = new ArrayList<>();
    /**
     * The instance of ButtonManager which contains the tool GUI for this controller
     */
    public ButtonManager buttonManager;
    /**
     * The node that manages all the canvas panel tabs within the application
     */
    @FXML
    protected TabPane tabs;
    /**
     * The uppermost Node within the current scene
     */
    @FXML
    private AnchorPane root;
    /**
     * The instance of the BrickKeys class that manages KeyBinds for this controller
     */
    public BrickKeys keyBinds;
    /**
     * The toolbar in which all the tools GUI will be constructed under
     */
    @FXML
    private ToolBar toolBar;
    /**
     * The MenuBar in which all the menu GUI is constructed under
     */
    @FXML
    private MenuBar menuBar;

    /**
     * Helper function which return a value restricted between min and max
     *
     * @param val The double to evaluate
     * @param min The minimum value which to return
     * @param max The maximum value which to return
     * @return Double value between min and max
     */
    public static double clamp(double val, double min, double max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

    /**
     * Helper function which return a value restricted between min and max
     *
     * @param val The int to evaluate
     * @param min The minimum value which to return
     * @param max The maximum value which to return
     * @return Integer value between min and max
     */
    public static int clamp(int val, int min, int max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

    /**
     * Called when the program starts from the application class
     */
    protected void Start() {
        logger.info("[APP] Started Program Controller!");
        Scene scene = root.getScene();
        buttonManager = new ButtonManager(toolBar, this);
        toolBar.getStyleClass().add("ToolBorder");
        menuBar.getStyleClass().add("MenuBorder");
        keyBinds = new BrickKeys(scene, this);
        keyBinds.SetKeyBinds();
        addTab();
        try {
            Path path = Path.of(BrickSave.savePath);
            if (!Files.exists(path)) {
                boolean test = new File(BrickSave.savePath).mkdirs();
            }
        } catch (Exception e) {
            logger.error("[APP] Failed to make application directories!");
            e.printStackTrace();
        }
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
            logger.info("[APP] Created {} Tab!", getCanvas().Name);
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
     * Returns the current tool selection from the button manager as a BrickTools enum value
     *
     * @return int
     */
    public BrickTools getToolType() {
        return buttonManager.getSelectedToggle();
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
                logger.info("[{}] Cleared Canvas", this.getCanvas().Name);
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
            logger.info("[{}] Inserted Image File: {}", this.getCanvas().Name, imageFile.getName());
        }
    }

    /**
     * Handles saving an image from the canvasPanel, if it has not yet been saved will call SaveAs instead
     */
    @FXML
    protected void handleSaveImage() {
        buttonManager.startAutoSave();
        if (ImageFile.get(tabs.getSelectionModel().getSelectedIndex()) == null) {
            ImageFile.putIfAbsent(tabs.getSelectionModel().getSelectedIndex(),
                    BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name, logger));
            return;
        }
        BrickSave.saveImageFromNode(this.getCanvas().root, ImageFile.get(tabs.getSelectionModel().getSelectedIndex()),
                logger, this.getCanvas().Name);
    }

    /**
     * Handles saving an image to a user created file from the canvasPanel
     */
    @FXML
    protected void handleSaveImageAs() {
        ImageFile.putIfAbsent(tabs.getSelectionModel().getSelectedIndex(),
                BrickSave.saveImageASFromNode(this.getCanvas().root, this.getCanvas().Name, logger));
        buttonManager.startAutoSave();
    }

    /**
     * Will iterate through all open tabs and save them as a png using the name of the tab
     */
    protected void saveAll() {
        for (int i = 0; i <= tabs.getTabs().size() - 1; i++) {
            if (ImageFile.containsKey(i))
                BrickSave.saveImageFromNode(canvasPanels.get(i).root, ImageFile.get(i), logger, this.getCanvas().Name);
            else {
                String fullName = BrickSave.savePath + "\\" + canvasPanels.get(i).Name + ".png";
                ImageFile.putIfAbsent(i, new File(fullName));
                BrickSave.saveImageFromNode(canvasPanels.get(i).root, ImageFile.get(i), logger, this.getCanvas().Name);
            }
        }
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
            logger.info("[APP] Opened BickPaint About Menu");
        } catch (IOException e) {
            logger.error("[APP] Filed to Open BrickPaint About Menu!");
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
                    System.exit(0);
                }
                if (res.get().equals(save)) {
                    saveAll();
                    Platform.exit();
                    System.exit(0);
                }
            }
        } else {
            Platform.exit();
            System.exit(0);
        }
    }
}