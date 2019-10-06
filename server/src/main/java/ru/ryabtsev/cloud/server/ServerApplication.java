package ru.ryabtsev.cloud.server;

import ru.ryabtsev.cloud.common.NetworkSettings;

/**
 * Implements server application.
 */
public class ServerApplication {

    private static final String NETWORK_PREFERENCES_NODE = "/study/cloud/server/network";
    private static final NetworkSettings networkSettings = new NetworkSettings(NETWORK_PREFERENCES_NODE);

    public static void main(String[] args) {
        try {
            new Server(networkSettings).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        networkSettings.save();
    }
}
