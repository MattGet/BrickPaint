package com.example.brickpaint;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Point2D;

public abstract class ArtMath {

    public static void DrawRect(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1){//draw down & right
                gc.strokeRect(x1, y1, w, h);
            }else if (x2 >= x1 && y1 >= y2){//drawing up & right
                gc.strokeRect(x1, y2, w, h);
            }else if (x1 >= x2 && y2 >= y1){//draw down & left
                gc.strokeRect(x2, y1, w, h);
            }else {//draw up & left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }

    public static void DrawSquare(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        if (w > h) h = w;
        else w = h;
        {
            if (x2 >= x1 && y2 >= y1){//draw down & right
                gc.strokeRect(x1, y1, w, h);
            }else if (x2 >= x1 && y1 >= y2){//draw up & right
                gc.strokeRect(x1, y2, w, h);
            }else if (x1 >= x2 && y2 >= y1){//draw down & left
                gc.strokeRect(x2, y1, w, h);
            }else {//draw up & left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }

    public static void DrawCircle(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double dist = Point2D.distance(x1, y1, x2, y2);
        if (x2 >= x1 && y2 >= y1){//draw down & right
            gc.strokeOval(x1, y1, dist, dist);
        }else if (x2 >= x1 && y1 >= y2){//draw up & right
            gc.strokeOval(x1, y2, dist, dist);
        }else if (x1 >= x2 && y2 >= y1){//draw down & left
            gc.strokeOval(x2, y1, dist, dist);
        }else {//drawing diagonally up left
            gc.strokeOval(x2, y2, dist, dist);
        }
    }

    public static void DrawOval(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1){//draw down & right
                gc.strokeOval(x1, y1, w, h);
            }else if (x2 >= x1 && y1 >= y2){//draw up & right
                gc.strokeOval(x1, y2, w, h);
            }else if (x1 >= x2 && y2 >= y1){//draw down & left
                gc.strokeOval(x2, y1, w, h);
            }else {//draw up & left
                gc.strokeOval(x2, y2, w, h);
            }
        }
    }
}
