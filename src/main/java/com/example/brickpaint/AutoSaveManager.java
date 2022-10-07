package com.example.brickpaint;

import javafx.application.Platform;

import java.util.TimerTask;

/**
 * Threading implementation of a timer that updates a visual countdown and runs save all when completed
 */
public class AutoSaveManager extends TimerTask {

    public int time;
    final int interval;
    final ButtonManager controller;

    /**
     * Default constructor for the AutoSaveManager class
     *
     * @param numb The time interval in seconds between saves
     * @param main The ButtonManager class that owns this thread
     */
    public AutoSaveManager(int numb, ButtonManager main){
        interval = numb;
        controller = main;
        time = interval;
    }


    /**
     * The tasks that the Thread shall run, it will count down the timer until it hits 0 updating the label as needed
     * when it reaches 0 it will fire the saveAll method in the controller class
     */
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
