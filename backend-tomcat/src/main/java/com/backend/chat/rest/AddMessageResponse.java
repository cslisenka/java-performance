package com.backend.chat.rest;

public class AddMessageResponse {

    private final boolean success;

    public AddMessageResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}