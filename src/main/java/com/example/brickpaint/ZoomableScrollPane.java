package com.example.brickpaint;

// Code sourced from https://stackoverflow.com/a/44314455 with minor modifications

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import javafx.event.EventHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@DefaultProperty("content")
public class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 0.7;
    private double zoomIntensity = 0.02;
    private Node zoomNode;

    private InputHandler _inputHandler = new InputHandler();

    public ZoomableScrollPane() {
        super();
        this.zoomNode = new Group();
        setContent(outerNode(zoomNode));

        setPannable(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true); //center
        setFitToWidth(true); //center
        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        //if(_inputHandler.getActiveKeys().contains(KeyCode.CONTROL)){
            outerNode.setOnScroll(e -> {
                e.consume();
                onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
            });
       // }
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        super.getContent().setScaleX(scaleValue);
        super.getContent().setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = super.getContent().parentToLocal(zoomNode.parentToLocal(mousePoint));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = super.getContent().getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
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

