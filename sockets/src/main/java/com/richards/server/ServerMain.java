package com.richards.server;

public class ServerMain {
    private final static Integer PORT = 4300;
    public static void main(String[] args) {
        Server server  = new Server(PORT);
        new Thread(server::acceptClientConnections).start();
    }
}
