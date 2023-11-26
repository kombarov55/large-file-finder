package com.company.swing.event_system;

import com.company.util.Logger;

public class TickEverySecond {

    private static Thread workingThread;

    public static void start() {
        Logger.log("registering ticks");
        EventBus.subscribersOfSearchStarted.add(v -> {
            workingThread = new Thread(() -> {
                try {
                    int seconds = 0;

                    while (true) {
                        Thread.sleep(1000);
                        seconds += 1;
                        EventBus.onTickEverySecond(seconds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            workingThread.start();
        });

        EventBus.subscribersOfSearchEnded.add(v -> {
            if (workingThread != null && workingThread.isAlive() && !workingThread.isInterrupted()) {
                workingThread.interrupt();
            }
        });
    }

}
