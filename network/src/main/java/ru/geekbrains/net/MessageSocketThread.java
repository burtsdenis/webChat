package ru.geekbrains.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageSocketThread extends Thread {

    private Socket socket;
    private MessageSocketThreadListener listener;
    private boolean isClosed = false;
    private DataOutputStream out;
    private DataInputStream in;

    public MessageSocketThread(MessageSocketThreadListener listener, String name, Socket socket) {
        super(name);
        this.socket = socket;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        try {
             in = new DataInputStream(socket.getInputStream());
            while (!isInterrupted()) {
                listener.onMessageReceived(in.readUTF());
            }
        } catch (IOException e) {
            listener.onException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            if (!socket.isConnected() || socket.isClosed()) {
                listener.onException(new RuntimeException("Socked closed or not initialized"));
                return;
            }
             out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            listener.onException(e);
        }
    }

    public synchronized void close() {
        isClosed = true;
        interrupt();
        try {
            if (out != null) {
                out.close();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listener.onSocketClosed();
    }
}
