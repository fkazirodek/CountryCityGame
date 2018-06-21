package controller;

import java.util.Map;

import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.message.Message;
import model.message.OperationType;

/**
 * This is controller class for player account window
 * @author 
 *
 */
public class PlayerAccountController {
	
	private Client client;
	
	@FXML
	private Label playerLoginLabel;
	@FXML
	private Label playerPointsLabel;
	@FXML
	private Label allGamesPlayedLabel;
	@FXML
	private Label wonGamesLabel;
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public void initData() {
		setLoginAndPoints();
		setPlayedGames();
	}

	private void setPlayedGames() {
		Message message = new Message(OperationType.GET_PLAYED_GAMES);
		message.setSender(LoginController.getLoggedPlayer().getLogin());
		client.sendMessage(message);
		Map<String, String> values = client.getServerResponse().getValues();
		allGamesPlayedLabel.setText(values.get("allGames"));
		wonGamesLabel.setText(values.get("wonGames"));
	}

	private void setLoginAndPoints() {
		Message message = new Message(OperationType.GET_USER);
		message.addValue("login", LoginController.getLoggedPlayer().getLogin());
		client.sendMessage(message);
		Map<String, String> values = client.getServerResponse().getValues();
		playerLoginLabel.setText(values.get("login"));
		playerPointsLabel.setText(values.get("points"));
	}
	
}
