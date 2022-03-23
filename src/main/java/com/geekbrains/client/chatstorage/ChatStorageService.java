package com.geekbrains.client.chatstorage;

public interface ChatStorageService {
    void append(String message);

    void save();

    String load();
}
