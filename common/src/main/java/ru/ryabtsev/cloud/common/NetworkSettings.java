package ru.ryabtsev.cloud.common;

import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * Provides default network settings like default port number,
 * maximal message object size, etc.
 */
public class NetworkSettings {
    public static final int DEFAULT_PORT = 8088;
    public static final int MAXIMAL_HEADER_SIZE_IN_BYTES = 1000;
    public static final int MAXIMAL_PAYLOAD_SIZE_IN_BYTES = 50 * 1024 * 1024;

    private final String nodeName;

    private final int port;

    private final int maximalHeaderSize;

    private final int maximalPayloadSize;

    private final int maximalMessageSize;

    /**
     * Constructs network settings object with settings from given file.
     * @param nodeName name of Java preferences node which corresponding to settings object.
     */
    public NetworkSettings(final String nodeName) {
        this.nodeName = nodeName;
        Preferences preferences = Preferences.userRoot().node(nodeName);
        port = preferences.getInt("port", DEFAULT_PORT);
        maximalHeaderSize = preferences.getInt("maximalHeaderSize", MAXIMAL_HEADER_SIZE_IN_BYTES);
        maximalPayloadSize = preferences.getInt( "maximalPayloadSize", MAXIMAL_PAYLOAD_SIZE_IN_BYTES);
        maximalMessageSize = preferences.getInt("maximalMessageSize", maximalHeaderSize + maximalPayloadSize);
    }

    public int getPort() {
        return port;
    }

    public int getMaximalHeaderSize() {
        return maximalHeaderSize;
    }

    public int getMaximalPayloadSize() {
        return maximalPayloadSize;
    }

    public int getMaximalMessageSize() {
        return maximalMessageSize;
    }

    public void save() {
        Preferences preferences = Preferences.userRoot().node(nodeName);
        preferences.putInt("port", port);
        preferences.putInt("maximalHeaderSize", maximalHeaderSize);
        preferences.putInt( "maximalPayloadSize", maximalPayloadSize);
        preferences.putInt("maximalMessageSize", maximalMessageSize);
    }
}
