package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;

public class BrickKeys {

    private Scene scene;
    private BrickPaintController controller;

    public BrickKeys(Scene input, BrickPaintController main){
        scene = input;
        controller = main;
    }

    public void SetKeyBinds(){
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.R && key.isControlDown()) {
                this.reset();
            }
        });
    }

    public void reset(){
        Node node = controller.canvasPanel;
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
    }

}
