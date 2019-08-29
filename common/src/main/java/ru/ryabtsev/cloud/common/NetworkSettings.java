package ru.ryabtsev.cloud.common;

/**
 * Provides default network settings like default port number,
 * maximal message object size, etc.
 */
public class NetworkSettings {
    public static final int DEFAULT_PORT = 8088;

    public static final int MAXIMAL_HEADER_SIZE_IN_BYTES = 1000;

    public static final int MAXIMAL_PAYLOAD_SIZE_IN_BYTES = 50 * 1024 * 1024;

    public static final int MAXIMAL_MESSAGE_SIZE_IN_BYTES = MAXIMAL_HEADER_SIZE_IN_BYTES + MAXIMAL_PAYLOAD_SIZE_IN_BYTES;
}
