package com.geekbrains.client.chatstorage;

public interface ChatStorageService {
    void append(String message);

    void save();

    void load();
}
