package ru.geekbrains.webchat;


public class Message {
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
}
