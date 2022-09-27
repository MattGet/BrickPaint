package com.brickpaint2;

import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.SelectionTool;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles common image functions for javaFX applications
 *
 * @author matde
 */
public abstract class BrickImage {

    /**
     * Adds an image to an imageView component in javaFX, scales imageView to the size of input image
     *
     * @param image The image to insert into the canvas
     * @param panel The CanvasPanel Class to add the image to
     */
    public static void Insert(CanvasPanel panel, BufferedImage image) {
        int x = image.getWidth();
        int y = image.getHeight();
        panel.setSizeX(x);
        panel.setSizeY(y);
        panel.UpdateSize();
        SelectionTool tool = (SelectionTool) PaintToolBuilder
                .create(PaintToolType.SELECTION)
                .makeActiveOnCanvas(panel.canvas)
                .build();

        // ... then create a new selection from the pasted image
        tool.createSelection(image, new Point(0, 0));
    }
}
