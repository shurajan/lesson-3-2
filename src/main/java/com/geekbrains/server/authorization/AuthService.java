package com.geekbrains.server.authorization;

public interface AuthService {
    void start();
    String getNickNameByLoginAndPassword(String login, String password);
    void end();
    void changeNickName(String nickName, String newNickName);
}
