package ru.geekbrains.webchat;

public class ChatServer {
    public boolean start(int port) {
        System.out.println("Server started at port: " + port);
        return true;
    }

    public boolean stop() {
        System.out.println("Server stopped");
        return true;
    }
}
