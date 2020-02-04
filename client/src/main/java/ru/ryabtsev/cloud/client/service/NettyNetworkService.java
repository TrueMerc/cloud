package ru.ryabtsev.cloud.client.service;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.ryabtsev.cloud.client.ClientNetworkSettings;
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

    private ClientNetworkSettings networkSettings;

    public NettyNetworkService(final ClientNetworkSettings settings) {
        this.networkSettings = settings;
    }

    @Override
    public void start() {
        try {
            socket = new Socket(networkSettings.getHost(), networkSettings.getPort());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(
                    socket.getInputStream(),
                    networkSettings.getMaximalMessageSize()
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
        networkSettings.save();
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

    @Override
    public void saveSettings() {
        networkSettings.save();
    }
}

