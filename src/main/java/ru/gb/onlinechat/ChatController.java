package ru.gb.onlinechat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    private TextArea messageArea;

    @FXML
    private TextField messageField;


    @FXML
    public void checkButtonClick(ActionEvent actionEvent) {
        final String playerString = messageField.getText();
        if (playerString.isEmpty()) {
            return;
        }else{
            messageArea.appendText(playerString + "\n");
        }
    }
}