package com.example.brickpaint;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Point2D;

import static java.lang.Math.*;

/**
 * Handles the math required to draw various shapes on the canvas in different directions
 *
 * @author matde
 */
public abstract class ArtMath {

    /**
     * Draws a Rectangle in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawRect(double x1, double y1, double x2, double y2, GraphicsContext gc, boolean fill) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        javafx.geometry.Point2D point = getTopLeft(x1, y1, x2, y2);
        gc.strokeRect(point.getX(), point.getY(), w, h);
        if (fill) gc.fillRect(point.getX(), point.getY(), w, h);
    }
    /**
     * Draws a Rectangle in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawRoundedRect(double x1, double y1, double x2, double y2, GraphicsContext gc, boolean fill) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        double curveValue = w/3;
        javafx.geometry.Point2D point = getTopLeft(x1, y1, x2, y2);
        gc.strokeRoundRect(point.getX(), point.getY(), w, h, curveValue, curveValue);
        if (fill) gc.fillRoundRect(point.getX(), point.getY(), w, h, curveValue, curveValue);
    }

    /**
     * Draws a Square in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawSquare(double x1, double y1, double x2, double y2, GraphicsContext gc, boolean fill) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        if (w > h) h = w;
        else w = h;
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeRect(x1, y1, w, h);
                if (fill) gc.fillRect(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeRect(x1, y2, w, h);
                if (fill) gc.fillRect(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeRect(x2, y1, w, h);
                if (fill) gc.fillRect(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeRect(x2, y2, w, h);
                if (fill) gc.fillRect(x2, y2, w, h);
            }
        }
    }

    /**
     * Draws a Circle in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawCircle(double x1, double y1, double x2, double y2, GraphicsContext gc, boolean fill) {
        double dist = Point2D.distance(x1, y1, x2, y2);
        javafx.geometry.Point2D point = getTopLeft(x1, y1, x2, y2);
        gc.strokeOval(point.getX(), point.getY(), dist, dist);
        if (fill) gc.fillOval(point.getX(), point.getY(), dist, dist);
    }

    /**
     * Draws an oval in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawOval(double x1, double y1, double x2, double y2, GraphicsContext gc, boolean fill) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeOval(x1, y1, w, h);
                if (fill) gc.fillOval(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeOval(x1, y2, w, h);
                if (fill) gc.fillOval(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeOval(x2, y1, w, h);
                if (fill) gc.fillOval(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeOval(x2, y2, w, h);
                if (fill) gc.fillOval(x2, y2, w, h);
            }
        }
    }

    /**
     * Draws a Polygon in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param sides number of sides polygon should have
     * @param gc the graphics content to draw the shape on
     * @param fill boolean to determine if the shape should be filled in
     */
    public static void DrawPoly(double x1, double y1, double x2, double y2, int sides, GraphicsContext gc, boolean fill) {
        javafx.geometry.Point2D center = new javafx.geometry.Point2D((x1 + x2) / 2, (y1 + y2) / 2);
        double dist = Point2D.distance(x1, y1, x2, y2)/2;
        double start = Math.atan2(y2 - y1, x2 - x1);
        double[] xPoints = new double[sides];
        double[] yPoints = new double[sides];

        for (int i = 0; i <= sides -1; i++){
            xPoints[i] = center.getX() + (dist * cos((2* PI*i)/sides + start));
            yPoints[i] = center.getY() + (dist * sin((2* PI*i)/sides + start));
        }
        gc.strokePolygon(xPoints, yPoints, sides);
        if (fill) gc.fillPolygon(xPoints, yPoints, sides);
    }

    /**
     * Takes two points on a line and a value, if that value is between the two points the method returns true
     *
     * @param value1 first point
     * @param value2 second point
     * @param range value to test
     * @return true if value is within range false if not
     */
    public static boolean compare(double value1, double value2, double range){
        if (value1 >= value2){
            return abs(value1) - abs(value2) <= range;
        }
        else {
            return abs(value2) - abs(value1) <= range;
        }
    }

    /**
     * Takes in two points on a rectangle and will return the value of the top left point of the ractangle
     *
     * @param x1 point 1 x
     * @param y1 point 1 y
     * @param x2 point 2 x
     * @param y2 point 2 y
     * @return Point2D corresponding to the top left of the rectangle
     */
    public static javafx.geometry.Point2D getTopLeft(double x1, double y1, double x2, double y2){
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        if (x2 >= x1 && y2 >= y1) {                     //draw down & right
            return new javafx.geometry.Point2D(x1, y1);
        } else if (x2 >= x1) {                          //drawing up & right
            return new javafx.geometry.Point2D(x1, y2);
        } else if (y2 >= y1) {                          //draw down & left
            return new javafx.geometry.Point2D(x2, y1);
        } else {                                        //draw up & left
            return new javafx.geometry.Point2D(x2, y2);
        }
    }

    /**
     * @hidden
     *
     * function to find if given point
     * lies inside a given rectangle or not.
     */
    public static boolean FindPoint(double x1, double y1, double x2,
                                    double y2, javafx.geometry.Point2D point)
    {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        javafx.geometry.Point2D topLeft = getTopLeft(x1, y1, x2, y2);
        if (point.getX() > topLeft.getX() && point.getX() < topLeft.getX() + w &&
                point.getY() > topLeft.getY() && point.getY() < topLeft.getY() + h)
            return true;

        return false;
    }
}
