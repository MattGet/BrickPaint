package com.brickpaint2.jmonet.clipboard;

import com.brickpaint2.jmonet.canvas.PaintCanvas;
import com.brickpaint2.jmonet.clipboard.CanvasClipboardActionListener;

/**
 * A determiner of which JMonet canvas presently has focus; used by the {@link CanvasClipboardActionListener} to
 * indicate which canvas (if any) should receive actions.
 */
public interface CanvasFocusDelegate {

    /**
     * Invoked to retrieve the canvas currently in focus, which should receive cut, copy and paste actions.
     *
     * @return The canvas that should receive cut, copy and paste commands, or null if no canvas is currently in
     * focus.
     */
    PaintCanvas getCanvasInFocus();
}
