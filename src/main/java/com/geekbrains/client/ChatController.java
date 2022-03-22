package com.geekbrains.client;

import com.geekbrains.client.chatstorage.ChatStorageService;
import com.geekbrains.client.chatstorage.FileChatStorageService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private final Network network;
    private ChatStorageService storageService;
    private String content;
    @FXML
    private WebView textArea;
    @FXML
    private TextField messageField, loginField, nickNameField;
    @FXML
    private HBox messagePanel, authPanel, changeNickPanel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<String> clientList;



    public ChatController() {
        this.content = "";
        this.network = new Network(this);
    }

    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        textArea.setVisible(authenticated);
        changeNickPanel.setVisible(authenticated);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthenticated(false);
    }

    public void displayMessage(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String formattedMessage = "<p style=\"color:blue;\">" + text + "</p>";
                storageService.append(formattedMessage);
                content+= formattedMessage;
                textArea.getEngine().loadContent(content, "text/html");
            }
        });
    }

    public void displayPrivateMessage(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String formattedMessage = "<p style=\"color:red;\">" + text + "</p>";
                storageService.append(formattedMessage);
                content+= formattedMessage;
                textArea.getEngine().loadContent(content, "text/html");
            }
        });
    }

    public void displayClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                clientList.getItems().add(nickName);
            }
        });
    }


    public void removeClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().remove(nickName);
            }
        });
    }

    public boolean ifClientExist(String nickName) {
        return clientList.getItems().contains(nickName);
    }

    public void sendAuth(ActionEvent event) {
        boolean authenticated = network.sendAuth(loginField.getText(), passwordField.getText());
        if (authenticated) {
            this.storageService = new FileChatStorageService(loginField.getText());
            this.storageService.load();

            loginField.clear();
            passwordField.clear();
            setAuthenticated(true);


            clientList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            clientList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    // Your action here
                    System.out.println("Selected user: " + newValue);
                }
            });
        }
    }

    public void sendMessage(ActionEvent event) {
        int selectedIndex = clientList.getSelectionModel().getSelectedIndex();
        System.out.println(selectedIndex);
        if (selectedIndex >= 0) {
            String recipient = clientList.getSelectionModel().getSelectedItem();
            System.out.println("Rec - " + recipient + " : " + messageField.getText());
            network.sendMessage(recipient, messageField.getText());
            clientList.getSelectionModel().clearSelection(selectedIndex);
        } else {
            System.out.println("ALL: " + messageField.getText());
            network.sendMessage(messageField.getText());
        }
        messageField.clear();
    }

    public void close() {
        storageService.save();
        network.closeConnection();
    }

    public void changeNickName(ActionEvent actionEvent) {
        network.changeNickName(nickNameField.getText());
        nickNameField.clear();
    }
}
