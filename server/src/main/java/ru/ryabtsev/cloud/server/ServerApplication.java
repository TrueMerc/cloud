package ru.ryabtsev.cloud.server;

/**
 * Implements server application.
 */
public class ServerApplication {

    public static void main(String[] args) {
        try {
            new Server().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
