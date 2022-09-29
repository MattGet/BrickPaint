package com.example.brickpaint;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ButtonManager {
    private ToolBar parent;
    private List<ToggleButton> toggles = new ArrayList<>();

    private final Separator s1, s2, s3, s4, s5;

    private final VBox clipBoard, image, tools, shapes, style, color;

    private final ToggleButton tPointer, tPencil, tRainbow, tEraser, tLine, tRect, tSquare, tCircle, tEllipse, tPolygon, tCustom, tGrabber;
    private ToggleButton tClipboard, tSelect, tCrop, tPaste, tCut;

    private static final Image clip = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/clipboard-icon.png")));
    private static final Image poin= new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/pointer-icon.png")));
    private static final Image penc = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/pencil-icon.png")));
    private static final Image rain = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/rainbow.png")));
    private static final Image eras = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/eraser.png")));
    private static final Image lin = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Line.png")));
    private static final Image rec = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/rectangle.png")));
    private static final Image squa = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/square.png")));
    private static final Image circ = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/circle.png")));
    private static final Image elli = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/oval.png")));
    private static final Image poly = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/polygon.png")));
    private static final Image cust = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/customPoly.png")));
    private static final Image grab = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/dropper.png")));
    private static final Image selc = new Image(Objects.requireNonNull(BrickPaintApp.class.getResourceAsStream("Icons/Cursor-Select-icon.png")));

    public ButtonManager(ToolBar parent){
        tClipboard = new ToggleButton();
        tClipboard.setGraphic(new ImageView(clip));
        clipBoard = new VBox(tClipboard);

        s1 = new Separator(Orientation.VERTICAL);

        tSelect = new ToggleButton();
        tSelect.setGraphic(new ImageView(selc));
        image = new VBox(tSelect);

        s2 = new Separator(Orientation.VERTICAL);

        tPointer = new ToggleButton();
        tPointer.setGraphic(new ImageView(poin));
        tEraser = new ToggleButton();
        tEraser.setGraphic(new ImageView(eras));
        HBox v1 = new HBox(tPointer, tEraser);

        tPencil = new ToggleButton();
        tPencil.setGraphic(new ImageView(penc));
        tRainbow = new ToggleButton();
        tRainbow.setGraphic(new ImageView(rain));
        HBox v2 = new HBox(tPencil, tRainbow);

        tools = new VBox(v1, v2);

        s3 = new Separator(Orientation.VERTICAL);

        tLine = new ToggleButton();
        tLine.setGraphic(new ImageView(lin));
        tRect = new ToggleButton();
        tRect.setGraphic(new ImageView(rec));
        tSquare = new ToggleButton();
        tSquare.setGraphic(new ImageView(squa));
        HBox v3 = new HBox(tRect, tSquare);

        tCircle = new ToggleButton();
        tCircle.setGraphic(new ImageView(circ));
        tEllipse = new ToggleButton();
        tEllipse.setGraphic(new ImageView(elli));
        HBox v4 = new HBox(tCircle, tEllipse);

        tPolygon = new ToggleButton();
        tPolygon.setGraphic(new ImageView(poly));
        tCustom = new ToggleButton();
        tCustom.setGraphic(new ImageView(cust));
        HBox v5 = new HBox(tPolygon, tCustom);

        shapes = new VBox(tLine, v4, v5);

        s4 = new Separator(Orientation.VERTICAL);

        style = new VBox();

        s5 = new Separator(Orientation.VERTICAL);

        tGrabber = new ToggleButton();
        tGrabber.setGraphic(new ImageView(grab));
        color = new VBox();

    }
}
