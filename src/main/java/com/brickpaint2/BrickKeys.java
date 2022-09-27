package com.brickpaint2;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Handles keyboard input and methods for the BrickPaintController Class
 *
 * @author matde
 */
public class BrickKeys {


    /**
     * The scene that is controlled by the controller
     */
    private final Scene scene;

    /**
     * The Contoller that this class handles keybinds for
     */
    private final BrickPaintController controller;

    /**
     * The Instance of the Input Handler for this class that manages the dictionary of pressed keys
     */
    public InputHandler activeKeys;

    /**
     * Constructor for BrickKeys that defines the scene and controller
     *
     * @param input The scene in which to look for key events
     * @param main  The controller class that will "own" this instance of BrickKeys
     */
    public BrickKeys(Scene input, BrickPaintController main) {
        scene = input;
        controller = main;

        activeKeys = new InputHandler();
        scene.setOnKeyPressed(activeKeys);
        scene.setOnKeyReleased(activeKeys);
    }

    /**
     * Handles creation of the event handlers for key events and calls the appropriate method
     */
    public void SetKeyBinds() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.R && key.isControlDown()) {
                this.reset();
            }
            if (key.getCode() == KeyCode.N && key.isControlDown()) {
                controller.addTab();
            }

            if (key.getCode() == KeyCode.S && key.isControlDown() && key.isAltDown()) {
                controller.handleSaveImageAs();
            } else if (key.getCode() == KeyCode.S && key.isControlDown()) {
                controller.handleSaveImage();
            }

            if (key.getCode() == KeyCode.E && key.isControlDown() && key.isAltDown()) {
                controller.OnClose(new WindowEvent(scene.getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
            }
            if (key.getCode() == KeyCode.C && key.isControlDown() && key.isAltDown()) {
                controller.handleClear();
            }
            if (key.getCode() == KeyCode.I && key.isControlDown()) {
                controller.handleInsertImage();
            }
            if (key.getCode() == KeyCode.A && key.isControlDown() && key.isAltDown()) {
                controller.handleOpenAboutMenu();
            }
        });
    }

    /**
     * resets the view of the canvasPanel from the controller class, triggered with CTRL+R
     */
    public void reset() {
        Node node = controller.getCanvas().pane;
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
    }


    /**
     * Handles all input from the keyboard and maintains an active list of currently pressed keys
     *
     * @author matde
     */
    static class InputHandler implements EventHandler<KeyEvent> {
        /**
         * A list of all keys currently being pressed down by the user
         */
        final private Set<KeyCode> activeKeys = new HashSet<>();

        /**
         * Handles key events for when keys are pressed or released, will then add or remove them from the list
         *
         * @param event Key event from the Input class
         */
        @Override
        public void handle(KeyEvent event) {
            if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
                activeKeys.add(event.getCode());
            } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
                activeKeys.remove(event.getCode());
            }
        }

        /**
         * Getter for the active keys Set
         *
         * @return unmodifableSet
         */
        public Set<KeyCode> getActiveKeys() {
            return Collections.unmodifiableSet(activeKeys);
        }
    }
}