package com.richards.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

@Getter
public class ServerWorker implements  Runnable {
    private final Server server;
    private final Socket clientSocket;
    private String username;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClientConnections() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String read;
        while((read = bufferedReader.readLine()) != null) {
            String[] tokens = read.split(" ");
            if(tokens.length > 0) {
                String command = tokens[0];
                System.out.println("Command: " + command);
                switch (command) {
                    case "login" -> handleClientLogin(tokens);
                    case "logoff" -> {
                        handleClientLogoff();
                        return;
                    }
                    case "msg" -> {
                        tokens = read.split(" ", 2);
                        handleClientMessages(tokens);
                    }
                    default -> sendMessage("Unknown command!");
                }
            }
        }
        clientSocket.close();
    }

    private void sendMessage(String message) {
        try {
            if(username != null) {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write((message + "\n").getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClientMessages(String[] tokens) {
        if(tokens.length == 2) {
            String msgBody = tokens[1];
            List<ServerWorker> workers = server.getServerWorkers();
            workers.forEach(worker -> worker.sendMessage("msg " + this.username + " " + msgBody));
        }
    }

    private void handleClientLogoff() throws IOException {
        System.out.println("Logging off");
        List<ServerWorker> workers = server.getServerWorkers();
        workers.forEach(worker -> {
            if(worker.getUsername() != null && !username.equals(worker.getUsername()))
                worker.sendMessage(("offline " + username));
        });
        workers.remove(this);
        clientSocket.close();
    }

    private void handleClientLogin(String[] tokens) throws IOException {
        if(tokens.length == 2) {
            String username = tokens[1];
            if(username.length() > 2) {
                this.username = username;
                System.out.println("User: " + username + " has joined the group chat");
                sendMessage("Joined Chat");
                // current user receives online notifications of others
                List<ServerWorker> workers = server.getServerWorkers();

                workers.forEach(worker -> {
                    if(worker.getUsername() != null && !worker.getUsername().equals(username))
                        sendMessage(("online " + worker.getUsername()));
                });

                workers.forEach(worker -> {
                    if(worker.getUsername() != null && !worker.getUsername().equals(username))
                        worker.sendMessage("online " + username);
                });

            }else {
                sendMessage("Username length should be greater than 2");
            }
        }
    }
}
