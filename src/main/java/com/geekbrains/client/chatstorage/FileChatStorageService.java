package com.geekbrains.client.chatstorage;

import com.geekbrains.CommonConstants;

import java.io.*;
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
            if (messages.size() > 1) msgs.append(System.lineSeparator());
        }

        byte[] outData = msgs.toString().getBytes();
        try (BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream("history/history_" + login + ".txt"))) {
            out.write(outData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader("history/history_" + login + ".txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                messages.add(line);
            }
            String everything = sb.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Нет истории");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
