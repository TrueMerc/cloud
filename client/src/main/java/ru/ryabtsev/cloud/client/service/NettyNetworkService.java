package ru.ryabtsev.cloud.client.service;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.ryabtsev.cloud.common.message.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Implements network service using Netty networking package.
 */
public class NettyNetworkService implements NetworkService {
    private Socket socket;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    @Override
    public void start(final String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message to server.
     * @param message message to send.
     * @return true if the message is successfully sent or false if it isn't.
     */
    public boolean sendMessage(final Message message) {
        try {
            out.writeObject(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Message receiveMessage() throws ClassNotFoundException, IOException {
        Object object = in.readObject();
        return (Message) object;
    }
}

