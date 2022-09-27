package com.brickpaint2.jmonet.tools;

import com.brickpaint2.jmonet.model.PaintToolType;
import com.brickpaint2.jmonet.tools.base.BasicTool;

import java.awt.*;

/**
 * A no-op tool; does not modify the canvas in any way.
 */
public class ArrowTool extends BasicTool {

    /**
     * Tool must be constructed via {@link com.brickpaint2.jmonet.tools.builder.PaintToolBuilder} to handle dependency
     * injection.
     */
    ArrowTool() {
        super(PaintToolType.ARROW);
    }

    @Override
    public Cursor getDefaultCursor() {
        return Cursor.getDefaultCursor();
    }
}
