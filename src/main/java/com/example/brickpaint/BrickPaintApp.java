package com.example.brickpaint;

import com.almasb.fxgl.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;


public class BrickPaintApp extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BrickPaintApp.class.getResource("BrickPaint.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setTitle("Brick Paint");
        stage.setScene(scene);
        Image BrickIcon = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("BrickIcon.jpg")));
        stage.getIcons().add(BrickIcon);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}