package com.brickpaint2.jmonet.canvas;

import com.brickpaint2.jmonet.canvas.JMonetCanvas;
import com.brickpaint2.jmonet.canvas.PaintCanvas;
import com.brickpaint2.jmonet.canvas.Scratch;
import com.brickpaint2.jmonet.canvas.layer.ImageLayer;
import com.brickpaint2.jmonet.canvas.layer.ImageLayerSet;
import com.brickpaint2.jmonet.canvas.observable.CanvasCommitObserver;
import com.brickpaint2.jmonet.canvas.surface.AbstractPaintSurface;
import com.brickpaint2.jmonet.context.GraphicsContext;
import com.brickpaint2.jmonet.tools.util.MathUtils;
import com.brickpaint2.jmonet.transform.image.PixelTransform;
import com.brickpaint2.jmonet.transform.image.StaticImageTransform;
import io.reactivex.subjects.BehaviorSubject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A scrollable, Swing component that can be painted upon using the paint tools in {@link com.brickpaint2.jmonet.tools}. See
 * {@link JMonetCanvas} for a canvas with an undo/redo buffer.
 */
public abstract class AbstractPaintCanvas extends AbstractPaintSurface implements com.brickpaint2.jmonet.canvas.PaintCanvas {

    private final ArrayList<CanvasCommitObserver> observers = new ArrayList<>();
    private final BehaviorSubject<Integer> gridSpacingSubject = BehaviorSubject.createDefault(1);
    private final com.brickpaint2.jmonet.canvas.Scratch scratch;
    private Paint canvasBackground;

    public AbstractPaintCanvas(Dimension dimension) {
        super(dimension);
        scratch = new com.brickpaint2.jmonet.canvas.Scratch(dimension.width, dimension.height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCanvasSize(Dimension dimension) {
        setSurfaceDimension(dimension);

        if (scratch != null) {
            scratch.setSize(dimension.width, dimension.height);
        }

        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getCanvasSize() {
        return getSurfaceDimension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Marks this component safe for garbage collection; removes registered listeners and components.
     */
    public void dispose() {
        super.dispose();

        gridSpacingSubject.onComplete();
        observers.clear();
        setTransferHandler(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageLayer[] getImageLayers() {
        return new ImageLayer[]{
                new ImageLayer(getCanvasImage()),
                getScratch().getRemoveScratchLayer(),
                getScratch().getAddScratchLayer()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCanvas() {
        Rectangle clear = new Rectangle(new Point(), getCanvasSize());
        GraphicsContext g2 = scratch.getRemoveScratchGraphics(null, clear);
        g2.setColor(Color.WHITE);
        g2.fill(clear);

        commit(scratch.getLayerSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paint getCanvasBackground() {
        return canvasBackground;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCanvasBackground(Paint paint) {
        this.canvasBackground = paint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point convertViewPointToModel(Point p) {
        Point error = getScrollError();
        int gridSpacing = getGridSpacing();
        double scale = getScaleObservable().blockingFirst();

        int x = p.x - error.x;                                          // Adjust for ignored scroll offset
        x = MathUtils.nearestFloor(x, (int) (gridSpacing * scale));     // Snap to grid
        x = (int) (x / scale);                                          // Adjust for scaling

        int y = p.y - error.y;
        y = MathUtils.nearestFloor(y, (int) (gridSpacing * scale));
        y = (int) (y / scale);

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point convertModelPointToView(Point p) {
        Point error = getScrollError();
        double scale = getScaleObservable().blockingFirst();

        int x = (int) (p.x * scale) + error.x;
        int y = (int) (p.y * scale) + error.y;

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scratch getScratch() {
        return scratch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        commit(scratch.getLayerSet());
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridSpacing(int grid) {
        this.gridSpacingSubject.onNext(grid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGridSpacing() {
        return gridSpacingSubject.blockingFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScale(double scale) {
        super.setScale(scale);
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCanvasCommitObserver(CanvasCommitObserver observer) {
        observers.add(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeCanvasCommitObserver(CanvasCommitObserver observer) {
        return observers.remove(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getScrollError() {
        Rectangle viewRect = getSurfaceScrollController().getScrollRect();
        double scale = getScale();
        return new Point((int) (viewRect.x % scale), (int) (viewRect.y % scale));
    }

    protected void fireCanvasCommitObservers(PaintCanvas canvas, ImageLayerSet imageLayerSet, BufferedImage canvasImage) {
        for (CanvasCommitObserver thisObserver : observers) {
            thisObserver.onCommit(canvas, imageLayerSet, canvasImage);
        }
    }

    public abstract void transform(StaticImageTransform transform);

    public abstract void transform(PixelTransform transform);
}
