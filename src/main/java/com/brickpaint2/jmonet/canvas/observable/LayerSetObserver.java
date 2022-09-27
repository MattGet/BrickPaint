package com.brickpaint2.jmonet.canvas.observable;

import com.brickpaint2.jmonet.canvas.layer.ImageLayerSet;

/**
 * An observer of modifications to a {@link ImageLayerSet}.
 */
public interface LayerSetObserver {

    /**
     * Fired to indicate a {@link ImageLayerSet} was modified.
     * @param modified The ChangeSet that was modified.
     */
    void onLayerSetModified(ImageLayerSet modified);
}
