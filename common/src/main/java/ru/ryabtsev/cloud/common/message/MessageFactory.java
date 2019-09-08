package ru.ryabtsev.cloud.common.message;

public interface MessageFactory {

    /**
     * Produces client-server application messages (requests and responses).
     * @param operation - operation identifier.
     * @param args - arguments which used for
     */
    Message getMessage(Operations operation, Object ... args);
}
