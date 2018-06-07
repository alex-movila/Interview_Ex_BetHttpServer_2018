package com.alexm.ex.BetServer.httpserver;

import com.sun.net.httpserver.HttpServer;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

public class BetHttpServer {
    public static final String DEFAULT_HOST_NAME = "localhost";
    public static final int DEFAULT_PORT = 8001;

    private String hostName;
    private int port;
    private HttpServer httpServer;

    public BetHttpServer() {
        this(DEFAULT_HOST_NAME, DEFAULT_PORT);
    }

    public BetHttpServer(String hostName, int port) {
        this.hostName = hostName;
        this.port = port > 0 ? port : DEFAULT_PORT;
    }

    public static URI createUri(String hostName, int port) throws URISyntaxException {
        return new URI("http://" + hostName + ":" + port + "/");
    }

    public static URI createUri(String hostName, int port, String request) throws URISyntaxException {
        return new URI("http://" + hostName + ":" + port + "/" + request);
    }

    public void start() {
        System.out.println("Starting server...");

        URI serverUri;

        try {
            serverUri = createUri(hostName, port);
            InetSocketAddress socket = new InetSocketAddress(serverUri.getPort());
            if (serverUri.getHost() != null && serverUri.getHost().length() > 0)
                socket = new InetSocketAddress(serverUri.getHost(), serverUri.getPort());

            try {
                httpServer = HttpServer.create(socket, 0);
            } catch (BindException e) {
                throw e;
            }


            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.createContext(serverUri.getPath(),
                    new BetHttpHandler());
            httpServer.start();

            System.out.printf("Server listening at %s%n", serverUri);
        } catch (Throwable t) {
            System.out.printf("Server failed to start: %s%n", t.getMessage());
            throw new RuntimeException("Server failed to start", t);
        }
    }

    public void stop(int maxWaitSeconds) {
        System.out.println("stopping Server...");
        httpServer.stop(maxWaitSeconds);
    }

}
