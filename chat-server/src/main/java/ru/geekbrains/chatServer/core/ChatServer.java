package ru.geekbrains.chatServer.core;


import ru.geekbrains.chatLibrary.MessageLibrary;
import ru.geekbrains.net.*;

import java.net.Socket;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final ChatServerListener listener;
    private ClientSessionThread clientSession;
    private final Vector<ClientSessionThread> clients = new Vector<>();
    private AuthController authController;

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "Chat-Server-Socket-Thread", port, 5000);
        serverSocketThread.start();
        authController = new AuthController();
        authController.init();
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
        this.clientSession = new ClientSessionThread(this, "ClientSession" , socket);
        clients.add(this.clientSession);
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onSocketClosed() {
        logMessage("Socket closed");
    }

    @Override
    public void onSocketReady() {
        logMessage("Socket ready");
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

        if (clientSession.isAuthorized()) {
            processAuthorizedUserMessage(message);
        } else {
            processUnauthorizedUserMessage(message);
        }
    }

    private void processAuthorizedUserMessage(String message) {
        logMessage(message);
    }

    private void processUnauthorizedUserMessage(String message) {
        String[] arr = message.split(MessageLibrary.DELIMITER);
        if (arr.length < 4 ||
                !arr[0].equals(MessageLibrary.AUTH_METHOD) ||
                !arr[1].equals(MessageLibrary.AUTH_REQUEST)
        ) {
            clientSession.authError("Incorrect request: " + message);
            return;
        }
        String login = arr[2];
        String password = arr[3];
        String nickName = authController.getNickName(login, password);
        if (nickName == null) {
            clientSession.authDeny();
            return;
        }
        clientSession.authAccept(nickName);
    }

    private void logMessage(String message) {
        listener.onChatServerMessage(message);
    }
}

