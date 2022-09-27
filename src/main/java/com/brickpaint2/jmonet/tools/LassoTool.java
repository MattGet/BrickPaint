package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.SelectionTool;
import com.brickpaint2.jmonet.tools.base.SelectionToolDelegate;
import com.brickpaint2.jmonet.tools.cursors.CursorFactory;
import com.brickpaint2.jmonet.tools.selection.TransformableCanvasSelection;
import com.brickpaint2.jmonet.tools.selection.TransformableSelection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Selection tool allowing the user to draw a free-form selection path on the canvas.
 */
public class LassoTool extends SelectionTool implements TransformableSelection, TransformableCanvasSelection, SelectionToolDelegate {

    private Path2D selectionBounds;

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    LassoTool() {
        super(PaintToolType.LASSO);

        setDelegate(this);
        setBoundaryCursor(CursorFactory.makeLassoCursor());
    }

    /** {@inheritDoc} */
    @Override
    public void clearSelectionFrame() {
        selectionBounds = null;
    }

    /** {@inheritDoc} */
    @Override
    public void setSelectionFrame(Shape bounds) {
        selectionBounds = new Path2D.Double(bounds);
    }

    /** {@inheritDoc} */
    @Override
    public void addPointToSelectionFrame(Point initialPoint, Point newPoint, boolean isShiftKeyDown) {
        if (selectionBounds == null) {
            selectionBounds = new Path2D.Double();
            selectionBounds.moveTo(initialPoint.getX(), initialPoint.getY());
        }

        selectionBounds.lineTo(newPoint.x, newPoint.y);
    }

    /** {@inheritDoc} */
    @Override
    public void closeSelectionFrame(Point finalPoint) {
        selectionBounds.closePath();
    }

    /** {@inheritDoc} */
    @Override
    public Shape getSelectionFrame() {
        return selectionBounds;
    }

    /** {@inheritDoc} */
    @Override
    public void translateSelectionFrame(int xDelta, int yDelta) {
        selectionBounds.transform(AffineTransform.getTranslateInstance(xDelta, yDelta));
    }
}
