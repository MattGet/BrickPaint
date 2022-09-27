package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.LinearTool;
import com.brickpaint2.jmonet.tools.base.LinearToolDelegate;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Tool that draws straight lines on the canvas.
 */
public class LineTool extends LinearTool implements LinearToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    LineTool() {
        super(PaintToolType.LINE);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void drawLine(Scratch scratch, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2) {
        Line2D line = new Line2D.Float(x1, y1, x2, y2);

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, line);
        g.setPaint(paint);
        g.setStroke(stroke);
        g.draw(line);
    }
}
