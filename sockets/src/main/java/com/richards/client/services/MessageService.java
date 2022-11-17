package com.richards.client.services;

@FunctionalInterface
public interface MessageService {
    void onMessageReceived(String sender, String msgBody);
}
