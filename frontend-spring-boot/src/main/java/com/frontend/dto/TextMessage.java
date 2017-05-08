package com.frontend.dto;

public class TextMessage {

    private long id;
    private String name;
    private String message;
    private java.util.Date timestamp;

    public TextMessage() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = new java.util.Date(timestamp);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }
}
