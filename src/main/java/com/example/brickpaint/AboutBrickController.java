package com.example.brickpaint;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Controller class for the About Program Popup Window
 *
 * @author matde
 */
public class AboutBrickController {

    /**
     * The text within the About window
     */
    @FXML
    TextArea textArea;

    /**
     * Sets the text of the about window to the text supplied from the class's designated file
     */
    public void Setup() {
        String str;
        InputStream in = Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("ReleaseNotes.txt"));
        try {
            str = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textArea.setText(str);
    }
}
