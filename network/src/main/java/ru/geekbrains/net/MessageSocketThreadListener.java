package ru.geekbrains.net;

public interface MessageSocketThreadListener {

    void onMessageReceived(String message);
    void onException(Throwable throwable);;
}
