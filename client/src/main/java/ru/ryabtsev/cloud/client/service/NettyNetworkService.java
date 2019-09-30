package ru.ryabtsev.cloud.client.service;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Implements network service using Netty networking package.
 */
public class NettyNetworkService implements NetworkService {
    private  static Socket socket;
    private  static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    @Override
    public void start(final String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(
                    socket.getInputStream(),
                    NetworkSettings.MAXIMAL_MESSAGE_SIZE_IN_BYTES
            );
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

    @Override
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

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }
}

