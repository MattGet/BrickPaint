package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

public abstract class BrickImage {

    public static void Insert(ImageView imageView, Image image){
        double x = image.getWidth();
        double y = image.getHeight();

        imageView.setFitHeight(y);
        imageView.setFitWidth(x);
        imageView.setImage(image);
    }
}
