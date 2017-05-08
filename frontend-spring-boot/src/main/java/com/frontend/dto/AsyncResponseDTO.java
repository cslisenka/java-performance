package com.frontend.dto;

public class AsyncResponseDTO {

    private final String response;

    public AsyncResponseDTO(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
