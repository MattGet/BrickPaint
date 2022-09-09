package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;


/**
 * Handles keyboard input and methods for the BrickPaintController Class
 * @author matde
 */
public class BrickKeys {

    /**
     * The scene that is controlled by the controller
     */
    private Scene scene;

    /**
     * The Contoller that this class handles keybinds for
     */
    private BrickPaintController controller;

    /**
     * Constructor for BrickKeys that defines the scene and controller
     * @param input
     * @param main
     */
    public BrickKeys(Scene input, BrickPaintController main){
        scene = input;
        controller = main;
    }

    /**
     * Handles creation of the event handlers for key events and calls the appropriate method
     */
    public void SetKeyBinds(){
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.R && key.isControlDown()) {
                this.reset();
            }
        });
    }

    /**
     * resets the view of the canvasPanel from the controller class, triggered with CTRL+R
     */
    public void reset(){
        Node node = controller.canvasPanel;
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
    }

}
