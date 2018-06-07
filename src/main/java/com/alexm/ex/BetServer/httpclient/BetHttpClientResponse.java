package com.alexm.ex.BetServer.httpclient;

public class BetHttpClientResponse {
    private final String body;
    private final int status;

    public BetHttpClientResponse(String body, int status) {
        this.body = body;
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "BetHttpClientResponse{" +
                "body='" + body + '\'' +
                ", status=" + status +
                '}';
    }
}
