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

/**
 * This is controller class for register window. Class allows register new player.
 * @author Filip K.
 *
 */
public class RegisterController {
	
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

	@FXML
	public void register() {
		client = main.getClient();
		String login = loginTextField.getText();
		String password = passwordField.getText();
		
		Message message = new Message(OperationType.REGISTER);
		message.addValue("login", login);
		message.addValue("password", password);
		main.getClient().sendMessage(message);
		
		Map<String, String> values = client.getServerResponse().getValues();
		boolean isRegistered = Boolean.parseBoolean(values.get("register"));
		
		if(isRegistered) {
			main.hideRegisterWindow();
		} else {
			errorLabel.setText(values.get("error"));
		}
	}

}
