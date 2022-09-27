package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.BoundsTool;
import com.brickpaint2.jmonet.tools.base.BoundsToolDelegate;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Tool for drawing outlined or filled ovals/circles on the canvas.
 */
public class OvalTool extends BoundsTool implements BoundsToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    OvalTool() {
        super(PaintToolType.OVAL);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void strokeBounds(Scratch scratch, Stroke stroke, Paint paint, Rectangle bounds, boolean isShiftDown) {
        Ellipse2D oval = new Ellipse2D.Float(bounds.x, bounds.y, bounds.width, bounds.height);

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, oval);
        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(oval);
    }

    /** {@inheritDoc} */
    @Override
    public void fillBounds(Scratch scratch, Paint fill, Rectangle bounds, boolean isShiftDown) {
        GraphicsContext g = scratch.getAddScratchGraphics(this, null);
        g.setPaint(fill);
        g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
