package controller;

import java.util.Map;

import client.Client;
import client.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

/**
 * This is controller class for login window. Class allows login player to game window.
 * @author 
 *
 */
public class LoginController {
	
	private static final String LOGIN_KEY = "login";
	private static final String PASSWORD = "password";
	
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
	
	public void logoutPlayer() {
		loggedPlayer = null;
	}
	
	@FXML
	public void login() {
		client = main.getClient();
		String login = loginTextField.getText();
		String password = passwordField.getText();
		
		Message message = new Message(OperationType.LOGIN);
		message.addValue(LOGIN_KEY, login);
		message.addValue(PASSWORD, password);
		main.getClient().sendMessage(message);
		
		Map<String, String> values = client.getServerResponse().getValues();
		boolean isLogged = Boolean.parseBoolean(values.get(LOGIN_KEY));

		if(isLogged) {
			loggedPlayer = new Player(login, password);
			main.hideLoginWindow();
		} else {
			errorLabel.setText("Nie udało sie zalogować\n niepoprawe hasło lub login\n");
		}
	}
	
	@FXML
	public void showRegisterWindow() {
		main.showRegisterWindow();
	}

}
