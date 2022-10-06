package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles saving a snapshot image of a JavaFX Node with user created or predefined files
 *
 * @author matde
 */
public abstract class BrickSave {

    public static final String savePath = new String(System.getProperty("user.home") + "\\Documents\\BrickPaint\\Saved Images");

    /**
     * Takes a snapshot of a Node and saves it to the specified file
     *
     * @param node The Node from which to take a screenshot of
     * @param file The File to save the image to
     */
    public static void saveImageFromNode(Node node, File file) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        String type = file.toString().substring(file.toString().lastIndexOf(".") +1);
        System.out.println("file type = " + type);
        try {
            WritableImage imageToSave = node.snapshot(parameters, null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            if (type.equals("jpg")){
                bImage = pngTojpg(bImage);
            }
            else if (type.equals("bmp")){
                bImage = pngTobmp(bImage);
            }

            if (bImage != null) {
                ImageIO.write(bImage, type, file);

                if (node.getScene().getWindow().focusedProperty().get()){
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
            e.printStackTrace();
        }
    }

    /**
     * Takes a snapshot of a Node and saves it to a file created by the user. Will open the file explorer
     * and initially name the file based off the Name
     *
     * @param node The node from which to take a screenshot of
     * @param Name Optional path string from which to initially name the new file with
     * @return returns file that image was saved to, else returns null
     */
    public static File saveImageASFromNode(Node node, String Name) {
        if (Name == null) {
            Name = "";
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName(Name);
        fileChooser.setInitialDirectory(new File(savePath));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("TIFF", "*.tif"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        //try to open file chooser and save the specified image
        try {
            File file = fileChooser.showSaveDialog(node.getScene().getWindow());
            String type = file.toString().substring(file.toString().lastIndexOf(".") +1);
            System.out.println("File Type To Save = " + type);
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage imageToSave = node.snapshot(parameters, null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);
            //System.out.println("Image type = " + bImage.getType());
            //System.out.println("Data Type = " + bImage.getSampleModel().getDataType());
            //System.out.println("Bands = " + bImage.getSampleModel().getNumBands());
            if (type.equals("jpg")){
                bImage = pngTojpg(bImage);
            }
            else if (type.equals("bmp")){
                bImage = pngTobmp(bImage);
            }

            //for (String name: ImageIO.getReaderFormatNames()){
            //    System.out.println("Format: " + name);
            //}
            if (bImage != null) {
                if (ImageIO.write(bImage, type, file)){
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(file);
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
                }
                else {
                    System.out.println("No File Type Found!!");
                }
            }
            return null;
        }
        //else return null as no file was saved
        catch (IOException e) {
            return null;
        }
    }


    private static BufferedImage pngTojpg(BufferedImage image){
        if (image.getType() == 3 || image.getType() == 2){
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

            System.out.println("Changed File type to: " + newBufferedImage.getType());
            System.out.println("Data Type = " + newBufferedImage.getSampleModel().getDataType());
            System.out.println("Bands = " + newBufferedImage.getSampleModel().getNumBands());
            return newBufferedImage;
        }
        else return null;
    }
    private static BufferedImage pngTobmp(BufferedImage image){
        if (image.getType() == 3 || image.getType() == 2){
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

            System.out.println("Changed File type to: " + newBufferedImage.getType());
            System.out.println("Data Type = " + newBufferedImage.getSampleModel().getDataType());
            System.out.println("Bands = " + newBufferedImage.getSampleModel().getNumBands());
            return newBufferedImage;
        }
        else return null;
    }

    /*
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
