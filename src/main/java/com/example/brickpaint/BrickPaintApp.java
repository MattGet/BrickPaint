package com.example.brickpaint;

import com.almasb.fxgl.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class BrickPaintApp extends Application {
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
        controller.Start();
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, controller::OnClose);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}