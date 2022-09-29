package com.example.brickpaint;

import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ButtonManager {
    private ToolBar parent;
    private List<ToggleButton> toggles = new ArrayList<>();

    private Separator cb, im, to, sh, st;

    private VBox clipBoard, image, tools, shapes, style, color;

    private ToggleButton pointer, pencil, rainbow, eraser, line, rect, square, circle, ellipse, polygon, custom, grabber;

    private static File

    public ButtonManager(ToolBar parent){

    }
}
