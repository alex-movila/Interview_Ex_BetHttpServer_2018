package com.alexm.ex.BetServer.httpclient;

import com.alexm.ex.BetServer.httpserver.BetHttpServer;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpRequest.BodyPublisher;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class BetHttpClient {

    public static BetHttpClientResponse sendGetRequest(String request) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(BetHttpServer.createUri(BetHttpServer.DEFAULT_HOST_NAME, BetHttpServer.DEFAULT_PORT, request))
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());

        return new BetHttpClientResponse(response.body(), response.statusCode());
    }

    public static BetHttpClientResponse sendPostRequest(String request, String sessionId, String body) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(BetHttpServer.createUri(BetHttpServer.DEFAULT_HOST_NAME, BetHttpServer.DEFAULT_PORT, request + "?sessionkey=" + sessionId))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(BodyPublisher.fromString(body))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());

        return new BetHttpClientResponse(response.body(), response.statusCode());
    }


}
