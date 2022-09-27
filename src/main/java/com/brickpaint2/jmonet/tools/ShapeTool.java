package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.BoundsTool;
import com.brickpaint2.jmonet.tools.base.BoundsToolDelegate;
import com.brickpaint2.jmonet.tools.util.MathUtils;

import java.awt.*;

/**
 * Tool for drawing regular polygons ("shapes") based on a configurable number of sides. For example, triangles,
 * squares, pentagons, hexagons, etc.
 */
public class ShapeTool extends BoundsTool implements BoundsToolDelegate {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    ShapeTool() {
        super(PaintToolType.SHAPE);
        setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void strokeBounds(Scratch scratch, Stroke stroke, Paint paint, Rectangle bounds, boolean isShiftDown) {
        Polygon poly = MathUtils.polygon(getInitialPoint(), getAttributes().getShapeSides(), getRadius(), getRotationAngle(isShiftDown));

        GraphicsContext g = scratch.getAddScratchGraphics(this, stroke, poly);
        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(poly);
    }

    /** {@inheritDoc} */
    @Override
    public void fillBounds(Scratch scratch, Paint fill, Rectangle bounds, boolean isShiftDown) {
        GraphicsContext g = scratch.getAddScratchGraphics(this, null);
        g.setPaint(fill);
        g.fill(MathUtils.polygon(getInitialPoint(), getAttributes().getShapeSides(), getRadius(), getRotationAngle(isShiftDown)));
    }

    private double getRadius() {
        return MathUtils.distance(getInitialPoint(), getCurrentPoint());
    }

    private double getRotationAngle(boolean isShiftDown) {
        double degrees = MathUtils.angle(getInitialPoint().x, getInitialPoint().y, getCurrentPoint().x, getCurrentPoint().y);

        if (isShiftDown) {
            degrees = MathUtils.nearestRound(degrees, getAttributes().getConstrainedAngle());
        }

        return Math.toRadians(degrees);
    }
}
