package com.example.brickpaint;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class BrickPaintAppTest {

    Stage stage2;

    BrickPaintController controller;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     */
    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BrickPaintApp.class.getResource("BrickPaint.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Brick Paint");
        stage2 = stage;
        stage.setScene(scene);
        Image BrickIcon = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/BrickIcon.jpg")));
        stage.getIcons().add(BrickIcon);
        stage.setMaximized(true);
        controller = fxmlLoader.getController();
        String css = Objects.requireNonNull(this.getClass().getResource("styles.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, controller::OnClose);
        stage.show();
        controller.Start();

    }

    @Test
    public void isStageNamed(FxRobot robot){
        assertEquals(stage2.getTitle(), "Brick Paint");
    }

    @Test
    public void isStyleSheetCorrect(FxRobot robot){
        assertEquals(stage2.getScene().getStylesheets().get(0), Objects.requireNonNull(this.getClass().getResource("styles.css")).toExternalForm());
    }

    @Test
    public void isColorBlack(FxRobot robot){
        assertEquals(controller.buttonManager.colorPicker.getValue(), Color.BLACK);
    }

    @Test
    public void isAutoSaveOn(FxRobot robot){
        assertTrue(controller.buttonManager.tAutoSave.isSelected());
    }

    @Test
    public void testAutoSaveButton(FxRobot robot){
        robot.clickOn(1230, 50, MouseButton.PRIMARY);

        assertFalse(controller.buttonManager.tAutoSave.isSelected());
    }

    @Test
    public void testDefaultTabCreated(FxRobot robot){
       assertEquals(controller.tabs.getTabs().size(), 1);
    }

}