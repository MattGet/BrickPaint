package com.example.brickpaint;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


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

    public InputHandler activeKeys;

    /**
     * Constructor for BrickKeys that defines the scene and controller
     * @param input
     * @param main
     */
    public BrickKeys(Scene input, BrickPaintController main){
        scene = input;
        controller = main;

        activeKeys = new InputHandler();
        scene.setOnKeyPressed(activeKeys);
        scene.setOnKeyReleased(activeKeys);
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
        Node node = controller.canvasPanel.root;
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
    }


    class InputHandler implements EventHandler<KeyEvent> {
        final private Set<KeyCode> activeKeys = new HashSet<>();

        @Override
        public void handle(KeyEvent event) {
            if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
                activeKeys.add(event.getCode());
            } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
                activeKeys.remove(event.getCode());
            }
        }

        public Set<KeyCode> getActiveKeys() {
            return Collections.unmodifiableSet(activeKeys);
        }
    }
}
