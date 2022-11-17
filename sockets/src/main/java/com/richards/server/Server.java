package com.richards.server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Server {
    private final Integer PORT;
    private final List<ServerWorker> serverWorkers;
    public Server(Integer PORT) {
        this.PORT = PORT;
        serverWorkers = new ArrayList<>();
    }

    public void acceptClientConnections() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while(true) {
                System.out.println("Waiting for client connections...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " +clientSocket.getLocalAddress() +  " connected");
                ServerWorker serverWorker = new ServerWorker(this, clientSocket);
                serverWorkers.add(serverWorker);
                new Thread(serverWorker).start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
