package ru.geekbrains.webchat;


import java.text.SimpleDateFormat;

public class Message {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String message;
    private final String author;
    private final long date;

    public Message(String message, String author, long date) {
        this.message = message;
        this.author = author;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public long getDate() {

        return date;
    }

    @Override
    public String toString() {
        return format.format(this.date) + " " + this.getAuthor() + ": " + this.getMessage() + "\n";
    }
}
