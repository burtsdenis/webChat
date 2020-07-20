package ru.geekbrains.chatServer.data;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;
    private final String nickName;
    private final String password;

    public User(String login, String password, String nickName) {
        this.login = login;
        this.nickName = nickName;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public String getNickName() {
        return nickName;
    }
}
