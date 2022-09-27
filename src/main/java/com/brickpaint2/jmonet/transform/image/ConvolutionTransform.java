package com.brickpaint2.jmonet.transform.image;

import com.brickpaint2.jmonet.tools.util.ImageUtils;
import com.brickpaint2.jmonet.transform.image.StaticImageTransform;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Convolves an image by applying an image {@link Kernel} to each pixel.
 */
public class ConvolutionTransform implements StaticImageTransform {

    private final Kernel kernel;

    public ConvolutionTransform(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        BufferedImage destination = ImageUtils.newArgbOfSize(source);

        BufferedImageOp convolution = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        convolution.filter(source, destination);

        return destination;
    }
}
