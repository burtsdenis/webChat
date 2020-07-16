package ru.geekbrains.net;


import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Message implements Serializable {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String message;
    private final String author;
    private final long date;

    public Message(String message, String author, long date) {
        this.date = date;
        this.author = author;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return format.format(this.date) + " " + this.author + ": " + this.message + "\n";
    }
}
