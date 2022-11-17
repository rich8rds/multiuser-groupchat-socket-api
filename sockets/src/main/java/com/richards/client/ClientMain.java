package com.richards.client;

public class ClientMain {
    private static final String ADDRESS = "localhost";
    private static final Integer PORT = 4300;

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient(ADDRESS, PORT);

        if(chatClient.isConnectedToServer()) {
            System.out.println("Connected to server!");
            if(chatClient.isLoggedIn()) {
                System.out.println("Login Successful");
                chatClient.sendMessages();
            }
            else System.out.println("Login failed!");
        }
        else System.out.println("Connection failed!");
    }
}
