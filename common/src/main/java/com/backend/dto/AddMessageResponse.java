package com.backend.dto;

public class AddMessageResponse {

    private boolean success;

    public AddMessageResponse() {
    }

    public AddMessageResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}