package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private String nickName;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            authentication();
            readMessages();

        } catch (IOException exception) {
            throw new RuntimeException("Проблемы при создании обработчика");
        }
    }

    public String getNickName() {
        return nickName;
    }

    public void authentication() throws IOException {
        while (true) {
            String message = inputStream.readUTF();
            if (message.startsWith(ServerCommandConstants.AUTHENTICATION)) {
                String[] authInfo = message.split(" ");
                String nickName = server.getAuthService().getNickNameByLoginAndPassword(authInfo[1], authInfo[2]);
                if (nickName != null) {
                    if (!server.isNickNameBusy(nickName)) {
                        sendAuthenticationMessage(true);
                        this.nickName = nickName;
                        //server.broadcastMessage(nickName + " зашел в чат");
                        server.broadcastMessage(ServerCommandConstants.ENTER + " " + nickName);
                        sendMessage(server.getClients());
                        server.addConnectedUser(this);
                        return;
                    } else {
                        sendAuthenticationMessage(false);
                    }
                } else {
                    sendAuthenticationMessage(false);
                }
            }
        }
    }

    private void sendAuthenticationMessage(boolean authenticated) throws IOException {
        outputStream.writeBoolean(authenticated);
    }

    private void readMessages() throws IOException {
        while (true) {
            String messageInChat = inputStream.readUTF();
            System.out.println("от " + nickName + ": " + messageInChat);
            if (messageInChat.equals(ServerCommandConstants.EXIT)) {
                closeConnection();
                return;
            } else if (messageInChat.startsWith(ServerCommandConstants.NICKNAME)) {
                String[] messageInfo = messageInChat.split(" ");
                if (messageInfo.length == 2) {
                    //Проверяем, что nick не пустой
                    if (!messageInfo[1].trim().equals("") && !server.isNickNameBusy(messageInfo[1])) {
                        server.broadcastMessage(ServerCommandConstants.NICKNAME + " " + this.nickName
                                + " " + messageInfo[1]);
                        server.getAuthService().changeNickName(this.nickName, messageInfo[1]);
                        this.nickName = messageInfo[1];
                    }
                }
            } else if (messageInChat.startsWith(ServerCommandConstants.PRIVATE)) {
                String[] messageInfo = messageInChat.split(" ");

                StringBuilder msg = new StringBuilder(ServerCommandConstants.PRIVATE);
                msg.append(" ");
                msg.append(nickName);
                msg.append(" ");
                msg.append(messageInfo[1]);
                msg.append(" ");
                for (int i = 2; i < messageInfo.length; i++) {
                    msg.append(messageInfo[i]);
                    msg.append(" ");
                }
                server.privateMessage(nickName, messageInfo[1], msg.toString());

            } else {
                server.broadcastMessage(nickName + ": " + messageInChat);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void closeConnection() {
        server.disconnectUser(this);
        server.broadcastMessage(ServerCommandConstants.EXIT + " " + nickName);
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
