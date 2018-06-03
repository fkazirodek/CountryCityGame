package controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import application.Main;
import client.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

public class MainController {

	private Client client;
	private Main main;
	private AtomicInteger counter = new AtomicInteger(30);
	private Timeline timeline;

	@FXML
	private Button newGameBtn;
	@FXML
	private Button playerAccounBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private Button sendAnswersBtn;
	@FXML
	private Label timerLabel;
	@FXML
	private Label letterLabel;
	@FXML
	private Label opponentLabel;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label infoLabel;
	@FXML
	private TextField countryTextField;
	@FXML
	private TextField cityTextField;
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField animalTextField;

	public void setMain(Main main) {
		this.main = main;
	}

	public void initData() {
		client = main.getClient();
		setPlayerWindow();
		setNumberOfLoggedUsers();
	}
	
	@FXML
	public void newGame() {
		new Thread(() -> {
			Message message = new Message(OperationType.NEW_GAME);
			message.setSender(LoginController.getLoggedPlayer().getLogin());
			client.sendMessage(message);

			Message response = client.getServerResponse();
			Map<String, String> values = response.getValues();
			Platform.runLater(() -> {
				sendAnswersBtn.setDisable(false);
				opponentLabel.setText(values.get("opponent"));
				letterLabel.setText(values.get("letter"));
			});

			startTimer();
			Message answers = null;
			while (counter.get() >= 1) {
				try {
					Thread.sleep(100); // TODO better solution - wait(100)? not work becouse of exception
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sendAnswersBtn.setDisable(true);
			answers = sendAnswers();
			String opponent = answers.getValues().get(values.get("opponent"));
			String me = answers.getValues().get(message.getSender());

			// TODO show results to user
			System.out.println("me = " + me);
			System.out.println("opponent = " + opponent);
		}).start();
	}
	
	@FXML
	public Message sendAnswers() {
		Message message = new Message(OperationType.WORDS);
		String country = countryTextField.getText();
		String city = cityTextField.getText();
		String name = nameTextField.getText();
		String animal = animalTextField.getText();
		message.setSender(LoginController.getLoggedPlayer().getLogin());
		message.addValue("country", country);
		message.addValue("city", city);
		message.addValue("name", name);
		message.addValue("animal", animal);
		client.sendMessage(message);
		return client.getServerResponse();
	}

	private void startTimer() {
		timeline = new Timeline(
				new KeyFrame(Duration.seconds(1), 
							 e -> {
								 if (counter.get() == 0) {
									 timeline.stop();
									 counter.set(30);
								 }
								 timerLabel.setText(String.valueOf(counter.getAndDecrement()));
							 })
		);
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	private void setPlayerWindow() {
		Player loggedPlayer = LoginController.getLoggedPlayer();
		if (loggedPlayer != null) {
			setLoggedUserWindow(loggedPlayer);
		} else {
			setUnknownUserWindow();
		}
	}

	private void setLoggedUserWindow(Player loggedPlayer) {
		welcomeLabel.setText("Zalogowany jako " + loggedPlayer.getLogin());
		logoutBtn.setText("Wyloguj");
		logoutBtn.setOnAction((e) -> {
			Message message = new Message(OperationType.LOGOUT);
			message.setSender(loggedPlayer.getLogin());
			client.sendMessage(message);
			boolean isLogout = Boolean.parseBoolean(client.getServerResponse()
															.getValues()
															.get("logout"));
			if(isLogout) {
				setUnknownUserWindow();
				setNumberOfLoggedUsers();
			}
		});
	}

	private void setUnknownUserWindow() {
		newGameBtn.setDisable(true);
		playerAccounBtn.setDisable(true);
		logoutBtn.setText("Zaloguj");
		welcomeLabel.setText("Niezalogowany");
		logoutBtn.setOnAction((e) -> main.showLoginWindow());
	}

	private void setNumberOfLoggedUsers() {
		Message mesage = new Message(OperationType.GET_NUM_OF_LOGGED_USERS);
		client.sendMessage(mesage);
		Message response = client.getServerResponse();
		int numberOfLoginUsers = Integer.parseInt(response.getValues().get("numLogin"));
		infoLabel.setText("Zalogowanych użytkowników: " + numberOfLoginUsers);
	}

}
