package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Handles saving a snapshot image of a JavaFX Node with user created or predefined files
 *
 * @author matde
 */
public abstract class BrickSave {

    public static final String savePath = System.getProperty("user.home") + "\\Documents\\BrickPaint\\Saved Images";

    /**
     * Takes a snapshot of a Node and saves it to the specified file
     *
     * @param node   The Node from which to take a screenshot of
     * @param file   The File to save the image to
     * @param Name   The name of the node/file being saved
     * @param logger The logger to log the save operation to
     */
    public static void saveImageFromNode(Node node, File file, Logger logger, String Name) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        String type = file.toString().substring(file.toString().lastIndexOf(".") + 1);
        //System.out.println("file type = " + type);
        try {
            WritableImage imageToSave = node.snapshot(parameters, null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            if (type.equals("jpg")) {
                bImage = pngTojpg(bImage);
            } else if (type.equals("bmp")) {
                bImage = pngTobmp(bImage);
            }

            if (bImage != null) {
                ImageIO.write(bImage, type, file);
                logger.info("[APP] Saved {} as file: {}", Name, file.toString());
                if (node.getScene().getWindow().focusedProperty().get()) {
                    Notifications.create()
                            .title("Saved Image")
                            .text(file.getName() + " successfully saved!")
                            .darkStyle()
                            .hideAfter(new Duration(4000))
                            .owner(node.getScene().getWindow())
                            .threshold(3, Notifications.create()
                                    .title("Saved Image")
                                    .text("successfully saved all open tabs!")
                                    .darkStyle()
                                    .hideAfter(new Duration(4000))
                                    .owner(node.getScene().getWindow()))
                            .show();
                }

            }
        } catch (IOException e) {
            logger.error("[APP] Encountered IOException when trying to save {} as a file", Name);
            e.printStackTrace();
        }
    }

    /**
     * Takes a snapshot of a Node and saves it to a file created by the user. Will open the file explorer
     * and initially name the file based off the Name
     *
     * @param node   The node from which to take a screenshot of
     * @param Name   Optional path string from which to initially name the new file with
     * @param logger The logger to log the save operation to
     * @return returns file that image was saved to, else returns null
     */
    public static File saveImageASFromNode(Node node, String Name, Logger logger) {
        if (Name == null) {
            Name = "";
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName(Name);
        fileChooser.setInitialDirectory(new File(savePath));
        FileChooser.ExtensionFilter PNG = new FileChooser.ExtensionFilter("PNG", "*.png");
        fileChooser.getExtensionFilters().addAll(PNG,
                new FileChooser.ExtensionFilter("TIFF", "*.tif"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        //try to open file chooser and save the specified image
        try {
            File file = fileChooser.showSaveDialog(node.getScene().getWindow());
            String type = file.toString().substring(file.toString().lastIndexOf(".") + 1);
            //System.out.println("File Type To Save = " + type);
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage imageToSave = node.snapshot(parameters, null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            //System.out.println("Image type = " + bImage.getType());
            //System.out.println("Data Type = " + bImage.getSampleModel().getDataType());
            //System.out.println("Bands = " + bImage.getSampleModel().getNumBands());
            if (type.equals("jpg")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().remove(ButtonType.OK);
                alert.getButtonTypes().remove(ButtonType.CANCEL);
                ButtonType Continue = new ButtonType("Continue");
                ButtonType Exit = new ButtonType("Exit");
                ButtonType Save = new ButtonType("Save as Png");
                alert.getButtonTypes().add(Save);
                alert.getButtonTypes().add(Exit);
                alert.getButtonTypes().add(Continue);
                alert.setHeaderText("Saving Your Drawing as a JPEG will remove its transparency and reduce color accuracy," +
                        " but will reduce its file size. \n\nDo you wish to continue?");
                alert.setTitle("Image Compression Requested");
                alert.initOwner(node.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals(Continue))
                        bImage = pngTojpg(bImage);
                    if (result.get().equals(Exit)) {
                        return null;
                    }
                    if (result.get().equals(Save)) {
                        file = changeExtension(file, "png");
                        type = "png";
                    }
                }
            } else if (type.equals("bmp")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().remove(ButtonType.OK);
                alert.getButtonTypes().remove(ButtonType.CANCEL);
                ButtonType Continue = new ButtonType("Continue");
                ButtonType Exit = new ButtonType("Exit");
                ButtonType Save = new ButtonType("Save as Png");
                alert.getButtonTypes().add(Save);
                alert.getButtonTypes().add(Exit);
                alert.getButtonTypes().add(Continue);
                alert.setHeaderText("Saving Your Drawing as a BMP will remove its transparency and reduce color accuracy.\n\n" +
                        "Do you wish to continue?");
                alert.setTitle("Bit Map Requested");
                alert.initOwner(node.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals(Continue))
                        bImage = pngTojpg(bImage);
                    if (result.get().equals(Exit)) {
                        return null;
                    }
                    if (result.get().equals(Save)) {
                        file = changeExtension(file, "png");
                        type = "png";
                    }
                }

            }

            //for (String name: ImageIO.getReaderFormatNames()){
            //    System.out.println("Format: " + name);
            //}
            if (bImage != null) {
                if (ImageIO.write(bImage, type, file)) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(file);
                    logger.info("[APP] Saved {} as file: {}", Name, file.toString());
                    Notifications.create()
                            .title("Saved Image")
                            .text(file.getName() + " successfully saved!")
                            .darkStyle()
                            .hideAfter(new Duration(4000))
                            .owner(node.getScene().getWindow())
                            .threshold(3, Notifications.create()
                                    .title("Saved Image")
                                    .text("successfully saved all open tabs!")
                                    .darkStyle()
                                    .hideAfter(new Duration(4000))
                                    .owner(node.getScene().getWindow()))
                            .show();
                    return file;
                } else {
                    logger.error("[APP] No File Type Found!!");
                }
            }
            return null;
        }
        //else return null as no file was saved
        catch (IOException e) {
            logger.error("[APP] Encountered IOException when trying to save {} as a file", Name);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper function that will modify the extension of a file
     *
     * @param f            File to Modify
     * @param newExtension The extension the file should be changed to
     * @return The renamed file
     */
    public static File changeExtension(File f, String newExtension) {
        int i = f.getName().lastIndexOf('.');
        String name = f.getName().substring(0, i + 1);
        return new File(f.getParent(), name + newExtension);
    }


    /**
     * Will convert a png image down to an image format compatible with the jpg Image Writer,
     * this will result in a loss of transparency
     *
     * @param image The image to convert
     * @return A new image with fewer data that can be saved as a jpg using the ImageIO Image Writer
     */
    private static BufferedImage pngTojpg(BufferedImage image) {
        if (image.getType() == 3 || image.getType() == 2) {
            BufferedImage newBufferedImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            // draw a white background and puts the originalImage on it.
            newBufferedImage.createGraphics()
                    .drawImage(image,
                            0,
                            0,
                            java.awt.Color.WHITE,
                            null);

            //System.out.println("Changed File type to jpeg");
            //System.out.println("Data Type = " + newBufferedImage.getSampleModel().getDataType());
            //System.out.println("Bands = " + newBufferedImage.getSampleModel().getNumBands());
            return newBufferedImage;
        } else return null;
    }

    /**
     * Will convert a png image down to an image format compatible with the bmp Image Writer,
     * this will result in a loss of transparency and color accuracy
     *
     * @param image The image to convert
     * @return A new image with fewer data that can be saved as a bmp using the ImageIO Image Writer
     */
    private static BufferedImage pngTobmp(BufferedImage image) {
        if (image.getType() == 3 || image.getType() == 2) {
            BufferedImage newBufferedImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);

            // draw a white background and puts the originalImage on it.
            newBufferedImage.createGraphics()
                    .drawImage(image,
                            0,
                            0,
                            java.awt.Color.WHITE,
                            null);

            //System.out.println("Changed File type to bmp");
            //System.out.println("Data Type = " + newBufferedImage.getSampleModel().getDataType());
            //System.out.println("Bands = " + newBufferedImage.getSampleModel().getNumBands());
            return newBufferedImage;
        } else return null;
    }

    /*
    Image Conversion Useful Info:

    Tiff can accept any type
    JPEG requires alpha be false and bit-depth between 1-8
    Bmp requires bands 3/1 and type 0 (byte)
    Png requires bands 1 or 3 w/o Alpha and 2 or 4 w/alpha and bit-depth between 1-16
    Gif requires 1 band and bit-depth between 1-8
    wBmp requires bit-depth of 1 and type of Byte, int, or Ushort


    Buffered Image Types
    See Also:
    BufferedImage,
    BufferedImage.TYPE_INT_RGB,
    BufferedImage.TYPE_INT_ARGB,
    BufferedImage.TYPE_INT_ARGB_PRE,
    BufferedImage.TYPE_INT_BGR,
    BufferedImage.TYPE_3BYTE_BGR,
    BufferedImage.TYPE_4BYTE_ABGR,
    BufferedImage.TYPE_4BYTE_ABGR_PRE,
    BufferedImage.TYPE_USHORT_565_RGB,
    BufferedImage.TYPE_USHORT_555_RGB,
    BufferedImage.TYPE_BYTE_GRAY,
    BufferedImage.TYPE_USHORT_GRAY,
    BufferedImage.TYPE_BYTE_BINARY,
    BufferedImage.TYPE_BYTE_INDEXED
     */
}
