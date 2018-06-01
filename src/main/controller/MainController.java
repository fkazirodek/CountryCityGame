package controller;

import application.Main;
import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

public class MainController {

	private Client client;
	private Main main;

	@FXML
	private Button newGameBtn;
	
	@FXML
	private Button playerAccounBtn;
	
	@FXML
	private Button logoutBtn;
	
	@FXML
	private Label timerLabel;

	@FXML
	private Label opponentLabel;

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label infoLabel;

	public void setMain(Main main) {
		this.main = main;
	}

	public void initData() {
		client = main.getClient();
		setLoggedPlayer();
		setNumberOfLoggedUsers();
	}

	private void setLoggedPlayer() {
		Player loggedPlayer = LoginController.getLoggedPlayer();
		if (loggedPlayer != null) {
			welcomeLabel.setText("Witaj " + loggedPlayer.getLogin());
			logoutBtn.setText("Wyloguj");
			logoutBtn.setOnAction((e) -> {
				
			});
		} else {
			newGameBtn.setDisable(true);
			playerAccounBtn.setDisable(true);
			logoutBtn.setText("Zaloguj");
			logoutBtn.setOnAction((e) -> main.showLoginWindow());
		}
	}

	private void setNumberOfLoggedUsers() {
		Message mesage = new Message(OperationType.GET_NUM_OF_LOGGED_USERS);
		client.sendMessage(mesage);
		Message response = client.getServerResponse();
		int numberOfLoginUsers = Integer.parseInt(response.getValues().get("numLogin"));
		infoLabel.setText("Zalogowanych użytkowników: " + numberOfLoginUsers);
	}

}
