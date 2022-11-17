package com.richards.client.services;

public interface UserStatusService {
    void online(String username);
    void offline(String username);
}
