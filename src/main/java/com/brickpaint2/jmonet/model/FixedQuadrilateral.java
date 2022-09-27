package com.brickpaint2.jmonet.model;

import com.brickpaint2.jmonet.model.Quadrilateral;

import java.awt.*;

/**
 * A model of a quadrilateral with fixed dimensions.
 */
@SuppressWarnings("unused")
public class FixedQuadrilateral implements Quadrilateral {

    private final Point topLeft;
    private final Point topRight;
    private final Point bottomLeft;
    private final Point bottomRight;

    public FixedQuadrilateral(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getTopLeft() {
        return topLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getTopRight() {
        return topRight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getBottomLeft() {
        return bottomLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getBottomRight() {
        return bottomRight;
    }
}
