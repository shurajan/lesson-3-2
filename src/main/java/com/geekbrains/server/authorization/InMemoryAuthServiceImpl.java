package com.geekbrains.server.authorization;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthServiceImpl implements AuthService {
    private final Map<String, UserData> users;

    public InMemoryAuthServiceImpl() {
        users = new HashMap<>();
        users.put("login1", new UserData("login1", "password1", "first_user"));
        users.put("login2", new UserData("login2", "password2", "second_user"));
        users.put("login3", new UserData("login3", "password3", "third_user"));
        users.put("login4", new UserData("login4", "password4", "forth_user"));
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации инициализирован");
    }

    @Override
    public synchronized String getNickNameByLoginAndPassword(String login, String password) {
        UserData user = users.get(login);
        // Ищем пользователя по логину и паролю, если нашли то возвращаем никнэйм
        if (user != null && user.getPassword().equals(password)) {
            return user.getNickName();
        }
        return null;
    }

    @Override
    public void end() {
        System.out.println("Сервис аутентификации отключен");
    }

    @Override
    public void changeNickName(String nickName, String newNickName) {
        for (Map.Entry<String, UserData> entry : users.entrySet()) {
            if (entry.getValue().getNickName().equals(nickName)) entry.getValue().setNickName(newNickName);
        }
    }
}
