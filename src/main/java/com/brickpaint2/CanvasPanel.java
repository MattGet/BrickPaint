package com.brickpaint2;

import com.defano.jmonet.canvas.JFXPaintCanvasNode;
import com.defano.jmonet.canvas.JMonetCanvas;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.PaintbrushTool;
import com.defano.jmonet.tools.base.Tool;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import com.defano.jmonet.tools.builder.StrokeBuilder;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.awt.*;


/**
 * Manages all the drawing Sub-nodes for the overarching canvas, may have multiple instances to create multiple canvases
 *
 * @author matde
 */
public class CanvasPanel {

    /**
     * The Name of the Panel
     */
    public final String Name;
    /**
     * Instance of the controller class for this canvas
     */
    private final BrickPaintController controller;
    /**
     * The parent Pane of this Canvas
     */
    private final TabPane parent;

    private final SnapshotParameters parameters = new SnapshotParameters();
    private final Integer cWidth = 1024;
    private final Integer cHeight = 1024;
    /**
     * The root Node that all of the canvas components are created under
     */
    public StackPane root = new StackPane();
    /**
     * The main canvas that will be used for drawing ect.
     */
    public JFXPaintCanvasNode canvas = new JFXPaintCanvasNode(new JMonetCanvas(new Dimension(cWidth, cHeight)));;
    /**
     * The viewport (scrollpane) that the entire canvas panel will reside within
     */
    public ScrollPane scrollPane;
    /**
     * Top level pane of the actual canvas objects and their corresponding stack pane
     */
    public AnchorPane pane = new AnchorPane();
    /**
     * Coordinates of the most recent mouse press
     */
    private Point2D initialTouch;
    /**
     * Coordinates of the mouse
     */
    private Point2D currTouch;
    /**
     * The instance of the zoom operator class that controls the movement and scale of this canvas panel
     */
    private AnimatedZoomOperator operator;
    /**
     * Manages Undo and Redo operations on this canvas
     */

    private int curr = 0;

    public Tool paintbrush;

    /**
     * Default Constructor
     *
     * @param tPane        The parent of this class
     * @param name         The Name of the Canvas Panel
     * @param keys         The instance of BrickKeys used for keybinds
     * @param controllerIn The controller class for this canvas
     */
    public CanvasPanel(TabPane tPane, String name, BrickKeys keys, BrickPaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Name = name;
        Setup(keys, name);
    }

    /**
     * Configures all of the settings of this Canvas Panel, should be called immediately after instantiation
     *
     * @param keys The instance of BickKyes that this class should use
     * @param name The name of the CanvasPanel
     */
    public void Setup(BrickKeys keys, String name) {

        AnchorPane canvasPane = new AnchorPane(canvas);
        canvasPane.setStyle("-fx-background-color: white;");

        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        root.setPrefWidth(cWidth);
        root.setPrefHeight(cHeight);
        root.getChildren().add(canvasPane);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        pane.getChildren().add(root);
        pane.setPrefHeight(cHeight);
        pane.setPrefWidth(cWidth);
        pane.getStyleClass().add("border");

        scrollPane = new ScrollPane(pane);
        scrollPane.setPrefViewportWidth(cWidth);
        scrollPane.setPrefViewportHeight(cHeight);
        Tab tab = new Tab(name, scrollPane);
        parent.getTabs().add(tab);
        AnchorPane.setBottomAnchor(scrollPane, (double) 0);
        AnchorPane.setTopAnchor(scrollPane, (double) 0);
        AnchorPane.setRightAnchor(scrollPane, (double) 0);
        AnchorPane.setLeftAnchor(scrollPane, (double) 0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        UpdateSize();

        pane.setOnScroll(this::onScroll);
        pane.setOnMouseDragged(this::onDrag);
        pane.setOnMousePressed(this::onMousePressed);
        pane.setOnMouseMoved(this::onMove);

        parameters.setFill(Color.TRANSPARENT);

        operator = new AnimatedZoomOperator(keys);

        paintbrush = PaintToolBuilder.create(PaintToolType.PAINTBRUSH)
                .withStroke(StrokeBuilder.withShape().ofCircle(8).build())
                .withStrokePaint(java.awt.Color.RED)
                .makeActiveOnCanvas(canvas)
                .build();
    }

    /**
     * Updates the size number of the current canvas in the toolbar
     */
    public void UpdateSize() {
        controller.cHeight.getEditor().setText(String.valueOf(canvas.getCanvas().getCanvasSize().height));
        controller.cWidth.getEditor().setText(String.valueOf(canvas.getCanvas().getCanvasSize().width));
    }

    /**
     * Sets the width of all necessary nodes in the canvas panel
     *
     * @param x new width
     */
    public void setSizeX(int x) {
        canvas.getCanvas().setCanvasSize(new Dimension(x, canvas.getCanvas().getCanvasSize().height));
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
    }

    /**
     * Sets the height of all necessary nodes in the canvas panel
     *
     * @param y new height
     */
    public void setSizeY(int y) {
        canvas.getCanvas().setCanvasSize(new Dimension(canvas.getCanvas().getCanvasSize().width, y));
        root.setPrefHeight(y);
        pane.setPrefHeight(y);
    }


    /**
     * Handles all actions that should occur when the mouse is pressed down, also records the pointer position
     *
     * @param event Mouse Event from Input class
     */
    private void onMousePressed(MouseEvent event) {
        initialTouch = new Point2D(event.getX(), event.getY());
        if (controller.getToolType() == BrickTools.ColorGrabber) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage snap = canvas.snapshot(parameters, null);
            PixelReader reader = snap.getPixelReader();
            controller.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
            root.getScene().setCursor(Cursor.DEFAULT);
            controller.resetGrabber();
        }


        switch (controller.getToolType()){
            case Pointer -> {
                paintbrush.deactivate();
            }
            case Pencil ->
            {
                paintbrush.activate(canvas.getCanvas());
            }
        }
    }




    /**
     * Clears the canvas panel
     */
    public void clearAll() {
        canvas.getCanvas().clearCanvas();
    }

    /**
     * Handles any actions which require the OnMove mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onMove(MouseEvent event) {
        if (controller.getToolType() == BrickTools.ColorGrabber) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage snap = canvas.snapshot(parameters, null);
            PixelReader reader = snap.getPixelReader();
            controller.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
        }
    }


    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onDrag(MouseEvent event) {
        currTouch = new Point2D(event.getX(), event.getY());
        //System.out.println(controller.getToolType());
        switch (controller.getToolType()) {
            case Pointer -> {
                double zoomFactor = 1.5;
                if (event.getY() <= 0) {
                    // zoom out
                    zoomFactor = 1 / zoomFactor;
                }
                operator.pan(pane, zoomFactor, event.getSceneX(), event.getSceneY());
            }
        }
    }

    /**
     * Handles zooming/scaling the canvasPanel with the AnimatedZoomOperator class
     *
     * @param event Scroll event from Input class
     */
    public void onScroll(ScrollEvent event) {
        double zoomFactor = 1.5;
        if (event.getDeltaY() <= 0) {
            // zoom out
            zoomFactor = 1 / zoomFactor;
        }
        operator.zoom(pane, zoomFactor, event.getSceneX(), event.getSceneY());
        UpdateSize();
    }
}
