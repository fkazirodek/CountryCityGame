package controller;

import java.util.Map;

import application.Main;
import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

public class LoginController {
	
	private static Player loggedPlayer;
	private Client client;
	
	@FXML
	private TextField loginTextField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Label errorLabel;
	
	private Main main;
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public static Player getLoggedPlayer() {
		return loggedPlayer;
	}
	
	@FXML
	public void login() {
		client = main.getClient();
		String login = loginTextField.getText();
		String password = passwordField.getText();
		
		Message message = new Message(OperationType.LOGIN);
		message.addValue("login", login);
		message.addValue("password", password);
		main.getClient().sendMessage(message);
		
		Map<String, String> values = client.getServerResponse().getValues();
		boolean isLogged = Boolean.parseBoolean(values.get("login"));

		if(isLogged) {
			loggedPlayer = new Player(login, password);
			main.hideLoginWindow();
		} else {
			errorLabel.setText("Nie udało sie zalogować,\n niepoprawe hasło\n");
		}
		
	}

}
