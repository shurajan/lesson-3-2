package com.geekbrains.server.chatstorage;

public interface ChatStorageService {

    void save(String nickName);

    void load(String nickName);
}
