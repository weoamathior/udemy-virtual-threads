package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting main");
        for (int i = 0; i < 100000; i++) {
            final int j = i;
            new Thread(() -> handleUserRequest(j)).start();
        }
        System.out.println("ending main");
    }

    private static void handleUserRequest(final int i) {
        System.out.println("Starting thread " + i + " " + Thread.currentThread());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Ending thread " + Thread.currentThread());
    }
}