package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.BoundsTool;
import com.brickpaint2.jmonet.tools.base.BoundsToolDelegate;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Tool for drawing outlined or filled rounded-rectangles on the canvas.
 */
public class RoundRectangleTool extends BoundsTool implements BoundsToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    RoundRectangleTool() {
        super(PaintToolType.ROUND_RECTANGLE);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void strokeBounds(Scratch scratch, Stroke stroke, Paint paint, Rectangle bounds, boolean isShiftDown) {
        int cornerRadius = getAttributes().getCornerRadius();
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius);

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, roundRect);
        g.setPaint(paint);
        g.setStroke(stroke);
        g.draw(roundRect);
    }

    /** {@inheritDoc} */
    @Override
    public void fillBounds(Scratch scratch, Paint fill, Rectangle bounds, boolean isShiftDown) {
        int cornerRadius = getAttributes().getCornerRadius();
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius);

        GraphicsContext g = scratch.getAddScratchGraphics(this, roundRect);
        g.setPaint(fill);
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius);
    }
}
