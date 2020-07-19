package ru.geekbrains.chatServer.data;

import java.io.Serializable;

public class User implements Serializable {
    private final String nickName;

    public User(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

}