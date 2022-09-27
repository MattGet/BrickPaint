package com.brickpaint2.jmonet.canvas;

import com.brickpaint2.jmonet.canvas.AbstractPaintCanvas;
import com.brickpaint2.jmonet.canvas.PaintCanvas;
import com.brickpaint2.jmonet.canvas.surface.Disposable;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;

/**
 * A trivial wrapper making a PaintCanvas available to JavaFX applications.
 */
public class JFXPaintCanvasNode extends SwingNode implements Disposable {

    private final com.brickpaint2.jmonet.canvas.AbstractPaintCanvas canvas;

    public JFXPaintCanvasNode(AbstractPaintCanvas canvas) {
        this.canvas = canvas;
        Platform.runLater(() -> JFXPaintCanvasNode.super.setContent(canvas));
    }

    public PaintCanvas getCanvas() {
        return canvas;
    }

    @Override
    public void dispose() {
        canvas.dispose();
    }
}
