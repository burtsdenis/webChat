package ru.geekbrains.chatServer.core;


import ru.geekbrains.net.*;

import java.net.Socket;

public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private MessageSocketThread socket;

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread = new ServerSocketThread(this,"Chat-Server-Socket-Thread", port, 5000);
        serverSocketThread.start();
    }

    public void stop() {
        if (serverSocketThread == null && !serverSocketThread.isAlive()) {
            return;
        }
        System.out.println("Server stopped");
        serverSocketThread.interrupt();
    }

    @Override
    public void onSocketAccepted(Socket socket) {
        this.socket = new MessageSocketThread(this, "ServerSocket", socket);
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onSocketClosed() {

    }

    @Override
    public void onSocketReady() {

    }

    @Override
    public void onClientTimeout(Throwable throwable) {

    }

    @Override
    public void onClientConnected() {
        System.out.println("Client connected");
    }

    @Override
    public void onMessageReceived(String message) {
        socket.sendMessage(message);
    }
}
