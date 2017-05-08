package com.frontend.dto;

public class MessageAddRequest {

    private final String name;
    private final String message;

    public MessageAddRequest(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}