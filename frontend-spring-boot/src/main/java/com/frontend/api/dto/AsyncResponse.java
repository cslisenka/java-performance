package com.frontend.api.dto;

public class AsyncResponse {

    private final String response;

    public AsyncResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
