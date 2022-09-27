package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.PathToolDelegate;
import com.brickpaint2.jmonet.tools.base.StrokedCursorPathTool;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Tool that erases pixels from the canvas by turning them back to fully transparent.
 */
public class EraserTool extends StrokedCursorPathTool implements PathToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    EraserTool() {
        super(PaintToolType.ERASER);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void startPath(Scratch scratch, Stroke stroke, Paint strokePaint, Point initialPoint) {
        scratch.erase(this, new Line2D.Float(initialPoint, initialPoint), stroke);
    }

    /** {@inheritDoc} */
    @Override
    public void addPoint(Scratch scratch, Stroke stroke, Paint strokePaint, Point lastPoint, Point thisPoint) {
        scratch.erase(this, new Line2D.Float(lastPoint, thisPoint), stroke);
    }

    @Override
    public void completePath(Scratch scratch, Stroke stroke, Paint strokePaint, Paint fillPaint) {
        // Nothing to do
    }
}
