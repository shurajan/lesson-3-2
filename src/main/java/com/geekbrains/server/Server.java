package com.geekbrains.server;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.authorization.AuthService;
import com.geekbrains.server.authorization.DBAuthServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final AuthService authService;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private List<ClientHandler> connectedUsers;

    public Server() {
        //authService = new InMemoryAuthServiceImpl();
        authService = new DBAuthServiceImpl();
        ExecutorService executorService = Executors.newFixedThreadPool(CommonConstants.MAX_NUMBER_CLIENTS);

        try (ServerSocket server = new ServerSocket(CommonConstants.SERVER_PORT)) {
            authService.start();
            connectedUsers = new ArrayList<>();

            while (true) {
                LOGGER.info("Сервер ожидает подключения");
                Socket socket = server.accept();
                executorService.execute(() -> {
                    new ClientHandler(this, socket);
                });

            }
        } catch (IOException exception) {
            LOGGER.error("Ошибка в работе сервера");
            LOGGER.error(exception.getMessage());
        } finally {
            LOGGER.info("Cервер завершает работу");
            executorService.shutdown();
            if (authService != null) {
                authService.end();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler handler : connectedUsers) {
            handler.sendMessage(message);
        }
    }

    public synchronized void privateMessage(String fromNickName, String toNickName, String message) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(toNickName) || handler.getNickName().equals(fromNickName)) {
                handler.sendMessage(message);
            }
        }
    }

    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
    }

    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
    }

    public String getClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (ClientHandler user : connectedUsers) {
            builder.append(user.getNickName()).append(" ");
        }

        return builder.toString();
    }
}
