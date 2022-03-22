package com.geekbrains.client.chatstorage;

import com.geekbrains.CommonConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class FileChatStorageService implements ChatStorageService {
    private final String login;

    Queue<String> messages = new LinkedList<>();

    public FileChatStorageService(String login) {
        this.login = login;
    }

    @Override
    public void append(String message) {
        if (messages.size() == CommonConstants.MESSAGE_LIMIT) {
            //удаляем первый элемент
            messages.remove();
        }
        messages.add(message);
    }

    @Override
    public void save() {

        StringBuilder msgs = new StringBuilder();

        for (String message : messages) {
            System.out.println(message);
            msgs.append(message);
            msgs.append("/n");
        }

        byte[] outData = msgs.toString().getBytes();
        try (FileOutputStream out = new FileOutputStream("history/history_" + login + ".txt")) {
            out.write(outData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {

    }
}
