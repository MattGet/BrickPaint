package com.brickpaint2.jmonet.clipboard;

import com.brickpaint2.jmonet.canvas.PaintCanvas;
import com.brickpaint2.jmonet.clipboard.CanvasFocusDelegate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A listener of clipboard-related actions (cut, copy and paste).
 */
@SuppressWarnings("unused")
public class CanvasClipboardActionListener implements ActionListener {

    private final com.brickpaint2.jmonet.clipboard.CanvasFocusDelegate delegate;

    public CanvasClipboardActionListener(CanvasFocusDelegate delegate) {
        this.delegate = delegate;
    }

    public CanvasClipboardActionListener() {
        this(new JMonetCanvasFocusDelegate());
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        PaintCanvas focusedCanvas = delegate == null ? null : delegate.getCanvasInFocus();

        if (focusedCanvas != null) {
            Action a = focusedCanvas.getActionMap().get(e.getActionCommand());
            if (a != null) {
                try {
                    a.actionPerformed(new ActionEvent(focusedCanvas, ActionEvent.ACTION_PERFORMED, null));
                } catch (Exception ignored) {
                    // Nothing to do
                }
            }
        }
    }

}
