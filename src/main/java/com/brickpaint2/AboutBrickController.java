package com.brickpaint2;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller class for the About Program Popup Window
 *
 * @author matde
 */
public class AboutBrickController {

    /**
     * The text file from which the text displayed in the about window is populated
     */
    private final String fileURL = "C:/Users/matde/Documents/Java Projects/CS250/BrickPaint/ReleaseNotes.txt";
    /**
     * The text within the About window
     */
    @FXML
    TextArea textArea;

    /**
     * Sets the text of the about window to the text supplied from the class's designated file
     */
    public void Setup() {
        Path path = Paths.get(fileURL);
        try {
            String text = Files.readString(path);
            textArea.setText(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
