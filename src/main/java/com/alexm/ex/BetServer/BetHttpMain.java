package com.alexm.ex.BetServer;


import com.alexm.ex.BetServer.httpserver.BetHttpServer;

/**
 * Simple Java non-blocking NIO webserver.
 *
 * @author md_5
 */
public class BetHttpMain {


    public static void main(String[] args) throws Exception {
        BetHttpServer betHttpServer = new BetHttpServer();
        // start the thread
        betHttpServer.start();

    }

}
