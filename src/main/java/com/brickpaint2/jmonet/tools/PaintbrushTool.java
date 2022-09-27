package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.PathToolDelegate;
import com.brickpaint2.jmonet.tools.base.StrokedCursorPathTool;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Tool for drawing free-form, textured paths on the canvas.
 */
public class PaintbrushTool extends StrokedCursorPathTool implements PathToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    PaintbrushTool() {
        super(PaintToolType.PAINTBRUSH);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void startPath(Scratch scratch, Stroke stroke, Paint strokePaint, Point initialPoint) {
        Line2D line = new Line2D.Float(initialPoint, initialPoint);

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, line);
        g.setStroke(stroke);
        g.setPaint(strokePaint);
        g.draw(line);

    }

    /** {@inheritDoc} */
    @Override
    public void addPoint(Scratch scratch, Stroke stroke, Paint strokePaint, Point lastPoint, Point thisPoint) {
        Line2D line = new Line2D.Float(lastPoint, thisPoint);

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, line);
        g.setStroke(stroke);
        g.setPaint(strokePaint);
        g.draw(line);
    }

    @Override
    public void completePath(Scratch scratch, Stroke stroke, Paint strokePaint, Paint fillPaint) {
        // Nothing to do
    }
}
