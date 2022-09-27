package com.brickpaint2.jmonet.tools.selection;

import com.brickpaint2.jmonet.tools.attributes.FillFunction;
import com.brickpaint2.jmonet.tools.selection.MutableSelection;
import com.brickpaint2.jmonet.tools.selection.TransformableSelection;
import com.brickpaint2.jmonet.transform.image.*;

import java.awt.*;

/**
 * A selection in which the pixels of the selected image can be transformed (i.e., change of brightness, opacity, etc.).
 * <p>
 * Differs from {@link TransformableSelection} in that these transforms do no change the selection shape (outline) or
 * location on the canvas; only the underlying selected image.
 */
public interface TransformableImageSelection extends MutableSelection, Transformable {

    /**
     * Performs a transformation on the selected image that does not effect the dimensions, bounds or location of the
     * selection.
     *
     * @param transform The transform to perform.
     */
    default void transform(StaticImageTransform transform) {
        if (hasSelection()) {
            setSelectedImage(transform.apply(getSelectedImage()));
            setDirty();
        }
    }

    /**
     * Performs a per-pixel transformation on all pixels bound by the selection.
     *
     * @param transform The transform operation to apply
     */
    default void transform(PixelTransform transform) {
        transform(new ApplyPixelTransform(transform, getIdentitySelectionFrame()));
    }

    /**
     * Fills all transparent pixels in the selection with the given fill paint.
     *
     * @param fillPaint    The paint to fill with.
     * @param fillFunction A method to fill pixels in the selected image
     */
    @Override
    default void fill(Paint fillPaint, FillFunction fillFunction) {
        transform(new FillTransform(getIdentitySelectionFrame(), fillPaint, fillFunction));
    }

}
