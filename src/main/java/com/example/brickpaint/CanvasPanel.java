package com.example.brickpaint;

import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


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
    public final SnapshotParameters parameters = new SnapshotParameters();
    /**
     * Instance of the controller class for this canvas
     */
    private final BrickPaintController controller;
    /**
     * The parent Pane of this Canvas
     */
    private final TabPane parent;
    private final Double CWIDTH = 1024d;
    private final Double CHEIGHT = 1024d;
    private final List<Point2D> polyLine = new ArrayList<>();
    //private final boolean validDragSelection = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * The root Node that all the canvas components are created under
     */
    public StackPane root = new StackPane();
    /**
     * The main canvas that will be used for drawing ect.
     */
    public Canvas canvas = new Canvas();
    /**
     * The viewport (ScrollPane) that the entire canvas panel will reside within
     */
    public ScrollPane scrollPane;
    /**
     * A dummy canvas used to draw objects with a live preview before they are drawn on the real canvas
     */
    public Canvas sketchCanvas = new Canvas();
    private final GraphicsContext sc = sketchCanvas.getGraphicsContext2D();
    /**
     * Top level pane of the actual canvas objects and their corresponding stack pane
     */
    public AnchorPane pane = new AnchorPane();
    /**
     * Manages Undo and Redo operations on this canvas
     */
    public UndoManager undoManager;
    private Group ScrollContent;
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
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private int curr = 0;
    private boolean drawingPolyLine = false;
    private boolean insideCanvas = false;
    private double select1, select2, select3, select4;
    private boolean useFill = false;
    private Image selection;

    /**
     * Default Constructor
     *
     * @param tPane        The parent of this class
     * @param name         The Name of the Canvas Panel
     * @param keys         The instance of BrickKeys used for key binds
     * @param controllerIn The controller class for this canvas
     */
    public CanvasPanel(TabPane tPane, String name, BrickKeys keys, BrickPaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Name = name;
        Setup(keys, name);
    }

    /**
     * Configures all the settings of this Canvas Panel, should be called immediately after instantiation
     *
     * @param keys The instance of BrickKeys that this class should use
     * @param name The name of the CanvasPanel
     */
    public void Setup(BrickKeys keys, String name) {

        sketchCanvas.setHeight(CHEIGHT);
        sketchCanvas.setWidth(CWIDTH);
        canvas.setHeight(CHEIGHT);
        canvas.setWidth(CWIDTH);

        AnchorPane canvasPane = new AnchorPane(canvas);
        AnchorPane sCPane = new AnchorPane(sketchCanvas);

        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        AnchorPane.setLeftAnchor(sketchCanvas, 0.0);
        AnchorPane.setRightAnchor(sketchCanvas, 0.0);
        AnchorPane.setTopAnchor(sketchCanvas, 0.0);
        AnchorPane.setBottomAnchor(sketchCanvas, 0.0);

        root.setPrefWidth(CWIDTH);
        root.setPrefHeight(CHEIGHT);
        root.getChildren().add(canvasPane);
        root.getChildren().add(sCPane);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        pane.getChildren().add(root);
        pane.setPrefHeight(CHEIGHT);
        pane.setPrefWidth(CWIDTH);
        pane.getStyleClass().add("border");


        ScrollContent = new Group(pane);
        scrollPane = new ScrollPane(ScrollContent);
        scrollPane.setPrefViewportWidth(CWIDTH);
        scrollPane.setPrefViewportHeight(CHEIGHT);
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
        pane.setOnMouseReleased(this::onMouseReleased);
        pane.setOnMouseMoved(this::onMove);
        pane.setOnMouseEntered(this::mouseEnter);
        pane.setOnMouseExited(this::mouseExit);

        parameters.setFill(Color.TRANSPARENT);

        operator = new AnimatedZoomOperator(keys);
        undoManager = new UndoManager();
        gc = canvas.getGraphicsContext2D();
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        controller.buttonManager.tFillShapes.selectedProperty().addListener(this::updateFill);
        this.useFill = controller.buttonManager.tFillShapes.isSelected();

        controller.tabs.getSelectionModel().select(tab);
    }

    /**
     * Will update whether to fill in shapes when the fill toggle is changed
     *
     * @param event selectedProperty Event Listener
     */
    private void updateFill(Observable event) {
        this.useFill = controller.buttonManager.tFillShapes.isSelected();
    }

    /**
     * Handles actions when the mouse enter the canvas area, used to update cursor
     *
     * @param event Mouse event
     */
    private void mouseEnter(MouseEvent event) {
        insideCanvas = true;
        controller.buttonManager.changeCursor();
    }

    /**
     * Handles when the mouse exits the canvas area, used to update cursor
     *
     * @param event Mouse event
     */
    private void mouseExit(MouseEvent event) {
        insideCanvas = false;
        root.getScene().setCursor(Cursor.DEFAULT);
    }

    /**
     * Updates the size number of the current canvas in the toolbar
     */
    public void UpdateSize() {
        controller.buttonManager.cHeight.getEditor().setText(String.valueOf(canvas.getHeight()));
        controller.buttonManager.cWidth.getEditor().setText(String.valueOf(canvas.getWidth()));
    }

    /**
     * Helper method which returns the canvas as an image
     *
     * @return Image of canvas
     */
    public Image fetchImage(){
        return canvas.snapshot(parameters, null);
    }

    /**
     * Sets the width of all necessary nodes in the canvas panel
     *
     * @param x new width
     */
    public void setSizeX(double x) {
        canvas.setWidth(x);
        sketchCanvas.setWidth(x);
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
        BrickPaintController.logger.info("[{}] Changed Canvas Width To {}", this.Name, x);
    }

    /**
     * Sets the height of all necessary nodes in the canvas panel
     *
     * @param y new height
     */
    public void setSizeY(double y) {
        canvas.setHeight(y);
        sketchCanvas.setHeight(y);
        root.setPrefHeight(y);
        pane.setPrefHeight(y);
        BrickPaintController.logger.info("[{}] Changed Canvas Height To {}", this.Name, y);
    }

    /**
     * Handles all actions that should occur when the mouse is pressed down, also records the pointer position
     *
     * @param event Mouse Event from Input class
     */
    private void onMousePressed(MouseEvent event) {
        initialTouch = new Point2D(event.getX(), event.getY());
        if (event.getButton() == MouseButton.PRIMARY) {
            switch (controller.getToolType()) {
                case Pencil, RainbowPencil, Eraser -> {
                    undoManager.LogU(this);
                    gc.beginPath();
                    gc.moveTo(event.getX(), event.getY());
                    gc.stroke();
                    genericLog();
                }
                case ColorGrabber -> {
                    SnapshotParameters parameters = new SnapshotParameters();
                    parameters.setFill(Color.TRANSPARENT);
                    WritableImage snap = canvas.snapshot(parameters, null);
                    PixelReader reader = snap.getPixelReader();
                    controller.buttonManager.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
                    root.getScene().setCursor(Cursor.DEFAULT);
                    controller.buttonManager.resetToggles();
                    genericLog();
                }
                case CustomShape -> {
                    initDraw(canvas.getGraphicsContext2D());
                    initDraw(sketchCanvas.getGraphicsContext2D());
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    if (polyLine.size() > 0) {
                        for (Point2D point : polyLine) {
                            if (ArtMath.compare(point.getX(), event.getX(), 10d) && ArtMath.compare(point.getY(), event.getY(), 10d)) {
                                gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), point.getX(), point.getY());
                                drawingPolyLine = false;
                                double[] xPoints = new double[polyLine.size()];
                                double[] yPoints = new double[polyLine.size()];
                                for (int i = 0; i <= polyLine.size() - 1; i++) {
                                    xPoints[i] = polyLine.get(i).getX();
                                    yPoints[i] = polyLine.get(i).getY();
                                }
                                if (useFill) gc.fillPolygon(xPoints, yPoints, polyLine.size());
                                polyLine.clear();
                                //System.out.println("Matched point");
                                genericLog();
                                return;
                            } else drawingPolyLine = true;
                        }
                        gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                    } else {
                        //System.out.println("First point");
                        undoManager.LogU(this);
                        drawingPolyLine = true;
                    }
                    polyLine.add(initialTouch);
                }
                case Line, Rectangle, RoundRectangle, Square, Circle, Oval, Polygon -> {
                    undoManager.LogU(this);
                    genericLog();
                }
            }
            if (controller.getToolType() != BrickTools.Pointer) {
                initDraw(gc);
                initDraw(sc);
            }
        }
        if (event.getButton() == MouseButton.SECONDARY) {
            if (controller.getToolType() == BrickTools.CustomShape) {
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                if (polyLine.size() <= 1) {
                    drawingPolyLine = false;
                    polyLine.clear();
                } else {
                    gc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                    polyLine.clear();
                    drawingPolyLine = false;
                }
            }
        }
    }

    /**
     * Handles Logging a generic action preformed on the canvas
     */
    private void genericLog() {
        if (insideCanvas) {
            BrickPaintController.logger.info("[{}] Using {} Tool", this.Name, controller.getToolType().toString());
        }
    }

    /**
     * Handles all actions that should occur when the mouse is released
     *
     * @param event Mouse Event from Input class
     */
    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            switch (controller.getToolType()) {
                case Line -> {
                    gc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Rectangle -> {
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case RoundRectangle -> {
                    ArtMath.DrawRoundedRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Square -> {
                    ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Circle -> {
                    ArtMath.DrawCircle(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Oval -> {
                    ArtMath.DrawOval(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case Polygon -> {
                    int sides = 6;
                    try {
                        sides = BrickPaintController.clamp(Integer.parseInt(controller.buttonManager.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ArtMath.DrawPoly(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sides, gc, useFill);
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                }
                case SelectionTool -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, false);
                    selection = getSubImage(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), this.canvas);
                    if (insideCanvas) {
                        select1 = initialTouch.getX();
                        select2 = initialTouch.getY();
                        select3 = event.getX();
                        select4 = event.getY();
                        //System.out.println("s3 = " + event.getX() + " s4 = " + event.getY());
                    }

                    //if(validDragSelection) validDragSelection = false;
                }
                case BucketFill -> {
                    FloodFill(event.getX(), event.getY());
                }
                default -> {
                }
            }
            if (insideCanvas){
                controller.buttonManager.updateServer();
            }
        }
    }

    /**
     * Converts the color-picker color to awt color format
     *
     * @return java.awt.Color
     */
    private java.awt.Color getColor() {
        Color c = controller.buttonManager.colorPicker.getValue();
        return new java.awt.Color((int) c.getRed(), (int) c.getGreen(), (int) c.getBlue());
    }

    /**
     * Preforms a flood/bucket fill operation on the canvas at the specified point
     *
     * @param x The x position of the point to start the flood fill
     * @param y The y position of the point to start the flood fill
     */
    public void FloodFill(double x, double y) {
        gc.setEffect(null);
        undoManager.LogU(this);
        double sensitivity = controller.buttonManager.fillSensitivity.getValue();
        Color setColor = controller.buttonManager.colorPicker.getValue();
        WritableImage canvasSnapshot = BrickImage.getScaledImage(this.canvas);
        Color startingColor = canvasSnapshot.getPixelReader().getColor((int) Math.floor(x), (int) Math.floor(y));
        FloodFill fill = new FloodFill(canvasSnapshot, (int) Math.floor(x),
                (int) Math.floor(y), startingColor, setColor, sensitivity);

        Future<WritableImage> result = executor.submit(fill);
        try {
            WritableImage img = result.get(3, TimeUnit.SECONDS);
            if (img != null) {
                genericLog();
                BrickImage.render(img, this.canvas, 0, 0, (int) img.getWidth(), (int) img.getHeight(), 0, 0);
                System.gc();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            BrickPaintController.logger.error("[{}] Flood Fill Operation Encountered a Fatal Error!", this.Name);
            throw new RuntimeException(e);
        }
    }


    /**
     * Writes the currently selected image to the system clipboard
     */
    public void selectionCopy() {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(selection);
            clipboard.setContent(content);
            BrickPaintController.logger.info("[{}] Copied Selected Image", this.Name);
            Notifications.create()
                    .title("Copy Image")
                    .text("Successfully Copied!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(root.getScene().getWindow())
                    .show();
        }
    }

    /**
     * Writes the currently selected image to the system clipboard and removes that area from the canvas
     */
    public void selectionCut() {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            undoManager.LogU(this);
            double x1 = select1;
            double y1 = select2;
            double x2 = select3;
            double y2 = select4;
            double w = Math.abs(x2 - x1);
            double h = Math.abs(y2 - y1);
            {
                if (x2 >= x1 && y2 >= y1) {         //draw down & right
                    gc.clearRect(x1, y1, w, h);
                } else //draw down & left
                    //draw up & left
                    if (x2 >= x1) {  //drawing up & right
                        gc.clearRect(x1, y2, w, h);
                    } else gc.clearRect(x2, Math.min(y2, y1), w, h);
            }

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(selection);
            clipboard.setContent(content);
            BrickPaintController.logger.info("[{}] Cut Selected Image", this.Name);
            Notifications.create()
                    .title("Cut Image")
                    .text("Successfully Copied!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(root.getScene().getWindow())
                    .show();
        }
    }

    /**
     * Draws the currently selected image to the canvas at the mouse position or top left of the canvas
     */
    public void selectionPaste() {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            if (selection != null) {
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                undoManager.LogU(this);
                Point2D point = new Point2D(currTouch.getX() - (selection.getWidth() / 2), currTouch.getY() - (selection.getHeight() / 2));
                if (insideCanvas) BrickImage.Paste(this, selection, point);
                else BrickImage.Paste(this, selection);
                BrickPaintController.logger.info("[{}] Pasted Selected Image", this.Name);
            }
        }
    }

    /**
     * Resizes the canvas to the selected image
     */
    public void selectionCrop() {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            if (selection != null) {
                undoManager.LogU(this);
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                BrickImage.Insert(this, selection);
                selection = null;
                BrickPaintController.logger.info("[{}] Cropped Selected Image", this.Name);
            }
        }
    }


    /**
     * Will mirror the selection vertically or horizontally based on the boolean input
     *
     * @param horizontal Mirrors horizontally if true else will mirror vertically
     */
    public void selectionMirror(boolean horizontal) {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            if (selection != null) {
                undoManager.LogU(this);
                BufferedImage image = SwingFXUtils.fromFXImage(selection, null);
                AffineTransform tx = new AffineTransform();
                if (horizontal) {
                    tx = AffineTransform.getScaleInstance(-1, 1);

                    tx.translate(-image.getWidth(null), 0);

                    BrickPaintController.logger.info("[{}] Mirrored Selected Image Horizontally", this.Name);
                } else {
                    tx = AffineTransform.getScaleInstance(1, -1);

                    tx.translate(0, -image.getHeight(null));

                    BrickPaintController.logger.info("[{}] Mirrored Selected Image Vertically", this.Name);
                }


                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

                image = op.filter(image, null);

                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());

                double x1 = select1;
                double y1 = select2;
                double x2 = select3;
                double y2 = select4;
                double w = Math.abs(x2 - x1);
                double h = Math.abs(y2 - y1);
                {
                    if (x2 >= x1 && y2 >= y1) {         //draw down & right
                        gc.clearRect(x1, y1, w, h);
                    } else //draw down & left
                        //draw up & left
                        if (x2 >= x1) {  //drawing up & right
                            gc.clearRect(x1, y2, w, h);
                        } else gc.clearRect(x2, Math.min(y2, y1), w, h);
                }

                Point2D point = ArtMath.getTopLeft(select1, select2, select3, select4);
                BrickImage.Paste(this, SwingFXUtils.toFXImage(image, null), point);
                selection = null;
            } else mirror(horizontal);
        }
    }

    /**
     * Will mirror the entire canvas vertically or horizontally based on the boolean input
     *
     * @param horizontal Mirrors horizontally if true else will mirror vertically
     */
    public void mirror(boolean horizontal) {
        if (canvas != null) {
            undoManager.LogU(this);
            Image screenshot = canvas.snapshot(parameters, null);
            BufferedImage image = SwingFXUtils.fromFXImage(screenshot, null);
            AffineTransform tx = new AffineTransform();
            if (horizontal) {
                tx = AffineTransform.getScaleInstance(-1, 1);

                tx.translate(-image.getWidth(null), 0);

                BrickPaintController.logger.info("[{}] Mirrored Canvas Horizontally", this.Name);
            } else {
                tx = AffineTransform.getScaleInstance(1, -1);

                tx.translate(0, -image.getHeight(null));

                BrickPaintController.logger.info("[{}] Mirrored Canvas Vertically", this.Name);
            }


            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            image = op.filter(image, null);

            sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            gc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            BrickImage.Paste(this, SwingFXUtils.toFXImage(image, null));
        }
    }

    /**
     * Will rotate the selection right or left 90 degrees based on the boolean input
     *
     * @param right Rotates right if true else rotates left
     */
    public void selectionRotate(boolean right) {
        if (controller.getToolType() == BrickTools.SelectionTool) {
            if (selection != null) {
                undoManager.LogU(this);
                BufferedImage image = SwingFXUtils.fromFXImage(selection, null);
                AffineTransformOp op;
                if (right) {
                    op = new AffineTransformOp(rotateClockwise90(image), AffineTransformOp.TYPE_BILINEAR);

                    BrickPaintController.logger.info("[{}] Rotated Selection 90 Degrees Clockwise", this.Name);
                } else {
                    op = new AffineTransformOp(rotateCounterClockwise90(image), AffineTransformOp.TYPE_BILINEAR);

                    BrickPaintController.logger.info("[{}] Rotated Selection 90 Degrees Counter Clockwise", this.Name);
                }

                image = op.filter(image, null);

                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());

                double x1 = select1;
                double y1 = select2;
                double x2 = select3;
                double y2 = select4;
                double w = Math.abs(x2 - x1);
                double h = Math.abs(y2 - y1);
                {
                    if (x2 >= x1 && y2 >= y1) {         //draw down & right
                        gc.clearRect(x1, y1, w, h);
                    } else //draw down & left
                        //draw up & left
                        if (x2 >= x1) {  //drawing up & right
                            gc.clearRect(x1, y2, w, h);
                        } else gc.clearRect(x2, Math.min(y2, y1), w, h);
                }

                Point2D point = ArtMath.getTopLeft(select1, select2, select3, select4);
                BrickImage.PasteRotate(this, SwingFXUtils.toFXImage(image, null), new Point2D(point.getX() + w / 2, point.getY() + h / 2));
                selection = null;
            } else rotate(right);
        }
    }

    /**
     * Will rotate the entire canvas right or left 90 degrees based on the boolean input
     *
     * @param right Rotates right if true else rotates left
     */
    public void rotate(boolean right) {
        if (canvas != null) {
            undoManager.LogU(this);
            Image screenshot = canvas.snapshot(parameters, null);
            BufferedImage image = SwingFXUtils.fromFXImage(screenshot, null);
            AffineTransformOp op;
            if (right) {
                op = new AffineTransformOp(rotateClockwise90(image), AffineTransformOp.TYPE_BILINEAR);

                BrickPaintController.logger.info("[{}] Rotated Canvas 90 Degrees Clockwise", this.Name);
            } else {
                op = new AffineTransformOp(rotateCounterClockwise90(image), AffineTransformOp.TYPE_BILINEAR);

                BrickPaintController.logger.info("[{}] Rotated Canvas 90 Degrees Counter Clockwise", this.Name);
            }

            image = op.filter(image, null);

            sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            gc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
            BrickImage.PasteRotate(this, SwingFXUtils.toFXImage(image, null), new Point2D(canvas.getWidth() / 2, canvas.getHeight() / 2));
        }
    }

    /**
     * Rotates clockwise 90 degrees. Uses rotation on center and then translating it to origin
     *
     * @param source buffered image
     * @return rotated buffered image
     * @see <a href="https://www.quickprogrammingtips.com/java/how-to-rotate-an-image-using-affine-transform-in-java.html">Source Code</a>
     */
    private AffineTransform rotateClockwise90(BufferedImage source) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.PI / 2, source.getWidth() / 2d, source.getHeight() / 2d);
        double offset = (source.getWidth() - source.getHeight()) / 2d;
        transform.translate(offset, offset);
        return transform;
    }

    /**
     * Rotates counter-clockwise 90 degrees. Uses rotation on center and then translating it to origin
     *
     * @param source buffered image
     * @return rotated buffered image
     * @see <a href="https://www.quickprogrammingtips.com/java/how-to-rotate-an-image-using-affine-transform-in-java.html">Source Code</a>
     */
    private AffineTransform rotateCounterClockwise90(BufferedImage source) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(-Math.PI / 2, source.getWidth() / 2d, source.getHeight() / 2d);
        double offset = (source.getWidth() - source.getHeight()) / 2d;
        transform.translate(-offset, -offset);
        return transform;
    }

    /**
     * Sets the parameters for drawing in the specified graphics content
     *
     * @param gc The graphics context to set parameters for
     */
    private void initDraw(GraphicsContext gc) {
        gc.setFill(controller.buttonManager.colorPicker.getValue());
        gc.setStroke(controller.buttonManager.colorPicker.getValue());
        double width = Double.parseDouble(controller.buttonManager.lineWidth.getEditor().getText());
        gc.setLineWidth(width);
        if (controller.buttonManager.lineStyle.getValue() == BrickTools.DashedLine) {
            //System.out.println("Setting dashed");
            gc.setLineDashes(width * 2, width * 2, width * 2, width * 2);
        } else {
            gc.setLineDashes(0d);
        }

        if (controller.getToolType() == BrickTools.Pencil || controller.getToolType() == BrickTools.RainbowPencil) {
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            BoxBlur blur = new BoxBlur();
            blur.setWidth(width / 4);
            blur.setHeight(width / 4);
            blur.setIterations(1);
            //gc.setEffect(blur);
        } else if (controller.getToolType() == BrickTools.Eraser) {
            gc.setEffect(null);
        } else if (controller.getToolType() == BrickTools.CustomShape) {
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
        } else if (controller.getToolType() == BrickTools.SelectionTool) {
            gc.setLineWidth(2);
            gc.setLineDashes(width / 2, width / 2, width / 2, width / 2);
            gc.setLineDashOffset(10);
            gc.setEffect(null);
            gc.setStroke(Color.LIGHTBLUE);
        } else {
            gc.setLineCap(StrokeLineCap.SQUARE);
            gc.setEffect(null);
        }
    }

    /**
     * Clears the canvas panel
     */
    public void clearAll() {
        undoManager.LogU(this);
        gc.setEffect(null);
        sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Notifications.create()
                .title("Clear Canvas")
                .text("Successfully Cleared!")
                .darkStyle()
                .hideAfter(new Duration(4000))
                .owner(root.getScene().getWindow())
                .show();
    }

    /**
     * Handles any actions which require the OnMove mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onMove(MouseEvent event) {
        currTouch = new Point2D(event.getX(), event.getY());
        if (controller.getToolType() == BrickTools.ColorGrabber) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage snap = canvas.snapshot(parameters, null);
            PixelReader reader = snap.getPixelReader();
            controller.buttonManager.colorPicker.setValue(reader.getColor((int) event.getX(), (int) event.getY()));
        }
        if (controller.getToolType() == BrickTools.CustomShape) {
            if (drawingPolyLine) {
                sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                if (polyLine.size() > 1) {
                    for (Point2D point : polyLine) {
                        if (ArtMath.compare(point.getX(), event.getX(), 10d) && ArtMath.compare(point.getY(), event.getY(), 10d)) {
                            sc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), point.getX(), point.getY());
                            return;
                        }
                    }
                    sc.strokeLine(polyLine.get(polyLine.size() - 1).getX(), polyLine.get(polyLine.size() - 1).getY(), event.getX(), event.getY());
                } else {
                    sc.strokeLine(polyLine.get(0).getX(), polyLine.get(0).getY(), event.getX(), event.getY());
                }
            }
        }
    }


    /**
     * Handles any actions which require the OnDrag mouse event within the canvas
     *
     * @param event Mouse Event from Input class
     */
    private void onDrag(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {

            //System.out.println(controller.getToolType());
            switch (controller.getToolType()) {
                case Pointer -> {

                }
                case RainbowPencil -> {
                    if (curr >= 5) {
                        gc.beginPath();
                        curr = 0;
                    } else curr++;
                    gc.setStroke(new Color(Math.random(), Math.random(), Math.random(), 1));
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
                case Pencil -> {
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
                case Eraser -> {
                    double size = Double.parseDouble(controller.buttonManager.lineWidth.getEditor().getText()) * 2;
                    gc.clearRect(event.getX() - size / 2, event.getY() - size / 2, size, size);
                }
                case Line -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    sc.strokeLine(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY());
                }
                case Rectangle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, useFill);
                }
                case RoundRectangle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRoundedRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, useFill);
                }
                case Square -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawSquare(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, useFill);
                }
                case Circle -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawCircle(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, useFill);
                }
                case Oval -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawOval(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, useFill);
                }
                case Polygon -> {
                    int sides = 6;
                    try {
                        sides = BrickPaintController.clamp(Integer.parseInt(controller.buttonManager.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        BrickPaintController.logger.error("[{}] PolySides Input was invalid!", this.Name);
                        e.printStackTrace();
                    }
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawPoly(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sides, sc, useFill);
                }
                case SelectionTool -> {
                    sc.clearRect(0, 0, sketchCanvas.getWidth(), sketchCanvas.getHeight());
                    ArtMath.DrawRect(initialTouch.getX(), initialTouch.getY(), event.getX(), event.getY(), sc, false);
                }
                default -> {
                }
            }
        }
    }

    /**
     * Returns the defined area of a Node as an image
     *
     * @param x1   xCord of mouse press
     * @param y1   yCord of mouse press
     * @param x2   xCord of mouse release
     * @param y2   yCord of mouse release
     * @param node node to take the image from
     * @return Image
     */
    private Image getSubImage(double x1, double y1, double x2, double y2, Node node) {
        int w = (int) Math.abs(x1 - x2);
        int h = (int) Math.abs(y1 - y2);
        if (w <= 0 || h <= 0) return null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage wImage = new WritableImage(w, h);
        Rectangle2D rect;
        if (x2 >= x1 && y2 >= y1) {                 //draw down & right
            rect = new Rectangle2D(x1, y1, w, h);
        } else if (x2 >= x1) {                      //drawing up & right
            rect = new Rectangle2D(x1, y2, w, h);
        } else if (y2 >= y1) {                      //draw down & left
            rect = new Rectangle2D(x2, y1, w, h);
        } else {                                    //draw up & left
            rect = new Rectangle2D(x2, y2, w, h);
        }
        parameters.setViewport(rect);
        return node.snapshot(parameters, wImage);
    }

    /**
     * Handles zooming/scaling the canvasPanel with the AnimatedZoomOperator class
     *
     * @param event Scroll event from Input class
     */
    public void onScroll(ScrollEvent event) {
        final double zoomFactor = event.getDeltaY() > 0 ? 1.2 : 1 / 1.2;
        operator.zoom(ScrollContent, pane, zoomFactor, event.getSceneX(), event.getSceneY(), scrollPane);
        UpdateSize();
    }
}
