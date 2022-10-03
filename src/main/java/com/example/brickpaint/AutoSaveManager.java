package com.example.brickpaint;

import javafx.application.Platform;

import java.util.TimerTask;

public class AutoSaveManager extends TimerTask {

    public int time;
    final int interval;
    final ButtonManager controller;

    public AutoSaveManager(int numb, ButtonManager main){
        interval = numb;
        controller = main;
        time = interval;
    }


    @Override
    public void run() {
        Platform.runLater(() ->{
            String result = "";
            int S = time % 60;
            int H = time / 60;
            int M = H % 60;
            if (M > 0){
                result = "Next AutoSave in " + M + " Min";
            }
            else {
                result = "Next AutoSave in " + S + " Seconds";
            }
            controller.aSaveTime.setText(result);
            controller.aSaveTime.setVisible(controller.tAutoSave.isSelected());
        });
        if (controller.tAutoSave.isSelected()) time--;

        if (time == 0){
            Platform.runLater(controller::AutoSave);
            Platform.runLater(() -> time = interval);
        }
    }
}
