package com.example.brickpaint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * Main application class
 *
 * @author matde
 */
public class BrickPaintApp extends Application {

    /**
     * start of the application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * start of javaFX program
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BrickPaintApp.class.getResource("BrickPaint.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Brick Paint");
        stage.setScene(scene);
        Image BrickIcon = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("BrickIcon.jpg")));
        stage.getIcons().add(BrickIcon);
        stage.setMaximized(true);
        BrickPaintController controller = fxmlLoader.getController();
        //Robot robot = new Robot();
        controller.Start();
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, controller::OnClose);
        stage.show();
    }
}