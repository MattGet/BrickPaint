package com.example.brickpaint;

import javafx.scene.canvas.GraphicsContext;

public abstract class ArtMath {

    public static void DrawRect(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1){//drawing diagonally down right
                gc.strokeRect(x1, y1, w, h);
            }else if (x2 >= x1 && y1 >= y2){//drawing diagonally up right
                gc.strokeRect(x1, y2, w, h);
            }else if (x1 >= x2 && y2 >= y1){//drawing diagonally down left
                gc.strokeRect(x2, y1, w, h);
            }else if (x1 >= x2 && y1 >= y2){//drawing diagnally up left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }

    public static void DrawSquare(double x1, double y1, double x2, double y2, GraphicsContext gc){
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        if (w > h) w = h;
        else h = w;
        {
            if (x2 >= x1 && y2 >= y1){//drawing diagonally down right
                gc.strokeRect(x1, y1, w, h);
            }else if (x2 >= x1 && y1 >= y2){//drawing diagonally up right
                gc.strokeRect(x1, y2, w, h);
            }else if (x1 >= x2 && y2 >= y1){//drawing diagonally down left
                gc.strokeRect(x2, y1, w, h);
            }else if (x1 >= x2 && y1 >= y2){//drawing diagnally up left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }
}
