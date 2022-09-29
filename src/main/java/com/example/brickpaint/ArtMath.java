package com.example.brickpaint;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Point2D;

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
     */
    public static void DrawRect(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeRect(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //drawing up & right
                gc.strokeRect(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeRect(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }

    /**
     * Draws a Square in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     */
    public static void DrawSquare(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        if (w > h) h = w;
        else w = h;
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeRect(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeRect(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeRect(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeRect(x2, y2, w, h);
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
     */
    public static void DrawCircle(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double dist = Point2D.distance(x1, y1, x2, y2);
        if (x2 >= x1 && y2 >= y1) {             //draw down & right
            gc.strokeOval(x1, y1, dist, dist);
        } else if (x2 >= x1 && y1 >= y2) {      //draw up & right
            gc.strokeOval(x1, y2, dist, dist);
        } else if (x1 >= x2 && y2 >= y1) {      //draw down & left
            gc.strokeOval(x2, y1, dist, dist);
        } else {                                //draw up & left
            gc.strokeOval(x2, y2, dist, dist);
        }
    }

    /**
     * Draws an oval in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param gc the graphics content to draw the shape on
     */
    public static void DrawOval(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeOval(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeOval(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeOval(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeOval(x2, y2, w, h);
            }
        }
    }

    /**
     * Draws a Rectangle in the desired direction based on the cursor location
     *
     * @param x1 start x cord
     * @param y1 start y cord
     * @param x2 mouse x cord
     * @param y2 mouse y cord
     * @param sides number of sides polygon should have
     * @param gc the graphics content to draw the shape on
     */
    public static void DrawPoly(double x1, double y1, double x2, double y2, int sides, GraphicsContext gc) {
        javafx.geometry.Point2D center = new javafx.geometry.Point2D((x1 + x2) / 2, (y1 + y2) / 2);
        double dist = Point2D.distance(x1, y1, x2, y2)/2;
        double[] xPoints = new double[sides];
        double[] yPoints = new double[sides];
        for (int i = 0; i <= sides -1; i++){
            xPoints[i] = center.getX() + (dist * Math.cos((2*Math.PI*i)/sides));
            yPoints[i] = center.getY() + (dist * Math.sin((2*Math.PI*i)/sides));
        }
        gc.strokePolygon(xPoints, yPoints, sides);
    }

    public static boolean compare(double value1, double value2, double range){
        if (value1 >= value2){
            if (Math.abs(value1) - Math.abs(value2) <= range){
                return true;
            }
            else return false;
        }
        else {
            if (Math.abs(value2) - Math.abs(value1) <= range){
                return true;
            }
            else return false;
        }
    }
}
