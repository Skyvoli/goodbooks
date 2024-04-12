package io.skyvoli.goodbooks.helper;

public class BackgroundTask {

    private final Thread task;

    public BackgroundTask(Runnable runnable) {
        task = new Thread(runnable);
    }

    public void start() {
        task.start();
    }
}