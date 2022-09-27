package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.attributes.ToolAttributes;
import com.brickpaint2.jmonet.tools.base.BasicTool;
import com.brickpaint2.jmonet.tools.builder.PaintToolBuilder;
import com.brickpaint2.jmonet.tools.cursors.CursorFactory;
import com.brickpaint2.jmonet.transform.image.FloodFillTransform;
import com.google.inject.Inject;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Tool that performs a flood-fill of all transparent pixels.
 */
@SuppressWarnings("unused")
public class FillTool extends BasicTool {

    @Inject
    private FloodFillTransform floodFill;

    /**
     * Tool must be constructed via {@link PaintToolBuilder} to handle dependency
     * injection.
     */
    FillTool() {
        super(PaintToolType.FILL);
    }

    @Override
    public Cursor getDefaultCursor() {
        return CursorFactory.makeBucketCursor();
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(MouseEvent e, Point imageLocation) {
        ToolAttributes attributes = getAttributes();
        Optional<Paint> fillPaint = attributes.getFillPaint();

        // Nothing to do if no fill paint is specified
        if (fillPaint.isPresent()) {
            getScratch().clear();

            floodFill.setFillPaint(fillPaint.get());
            floodFill.setOrigin(imageLocation);
            floodFill.setFill(attributes.getFillFunction());
            floodFill.setBoundaryFunction(attributes.getBoundaryFunction());

            getScratch().setAddScratch(floodFill.apply(getCanvas().getCanvasImage()), new Rectangle(getCanvas().getCanvasSize()));

            getCanvas().commit();
            getCanvas().repaint();
        }
    }

}
