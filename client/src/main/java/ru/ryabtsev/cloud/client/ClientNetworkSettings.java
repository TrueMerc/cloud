package ru.ryabtsev.cloud.client;

import ru.ryabtsev.cloud.common.NetworkSettings;

import java.util.prefs.Preferences;

/**
 * Implements client side network settings
 */
public class ClientNetworkSettings extends NetworkSettings {

    private static final String DEFAULT_HOST = "localhost";

    private final String host;

    /**
     * Constructs client side network settings.
      * @param nodeName client settings node name.
     */
    public ClientNetworkSettings(final String nodeName) {
        super(nodeName);
        Preferences preferences = Preferences.userRoot().node(nodeName);
        host = preferences.get("host", DEFAULT_HOST );
    }

    /**
     * Returns host name.
     * @return host name.
     */
    public String getHost() {
        return host;
    }
}
