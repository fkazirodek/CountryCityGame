package controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import client.Client;
import client.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

public class MainController {

	private Client client;
	private Main main;
	private AtomicInteger counter = new AtomicInteger(30);
	private Timeline timeline;
	private ExecutorService executorService;
		
	@FXML
	private Button newGameBtn;
	@FXML
	private Button playerAccounBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private Button sendAnswersBtn;
	@FXML
	private ImageView refreshImageView;
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
	@FXML
	private Label correctAnswerLabel;
	@FXML
	private Label pointsLabel;
	@FXML
	private Label opponentCountryLabel;
	@FXML
	private Label opponentCityLabel;
	@FXML
	private Label opponentNameLabel;
	@FXML
	private Label opponentAnimalLabel;
	@FXML
	private Label opponentCorrectAnswerLabel;
	@FXML
	private Label opponentPointsLabel;
	@FXML
	private ImageView countryImg;
	@FXML
	private ImageView cityImg;
	@FXML
	private ImageView nameImg;
	@FXML
	private ImageView animalImg;
	@FXML
	private ImageView opponentCountryImg;
	@FXML
	private ImageView opponentCityImg;
	@FXML
	private ImageView opponentNameImg;
	@FXML
	private ImageView opponentAnimalImg;
	@FXML
	private ImageView winnerImg;
	@FXML
	private ImageView opponentWinnerImg;
	
	public void setMain(Main main) {
		this.main = main;
	}

	public void initData() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		client = main.getClient();
		setPlayerWindow();
		setNumberOfLoggedUsers();
	}
	
	@FXML
	public void refreshLoggedUsers() {
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), refreshImageView);
		rotateTransition.setByAngle(180);
		rotateTransition.play();
		setNumberOfLoggedUsers();
	}
	
	@FXML
	public void newGame() {
		executorService.execute(() -> {
			setDisableTextFields(false);
			Message message = new Message(OperationType.NEW_GAME);
			message.setSender(LoginController.getLoggedPlayer().getLogin());
			client.sendMessage(message);

			Message response = client.getServerResponse();
			Map<String, String> values = response.getValues();
			sendAnswersBtn.setDisable(false);
			
			Platform.runLater(() -> {
				opponentLabel.setText(values.get("opponent"));
				letterLabel.setText(values.get("letter"));
			});

			startTimer();
			waitUntil(() -> counter.get() >= 1);
			
			sendAnswersBtn.setDisable(true);
			Message serverResponse = sendAnswers();
			setAnswers(serverResponse);
			setOpponentAnswers(values, serverResponse);
			setWinnerImg(serverResponse);
		});
	}
	
	@FXML
	public Message sendAnswers() {
		counter.set(0);
		sendAnswersBtn.setDisable(true);
		Message message = new Message(OperationType.WORDS);
		String country = countryTextField.getText();
		String city = cityTextField.getText();
		String name = nameTextField.getText();
		String animal = animalTextField.getText();
		setDisableTextFields(true);
		message.setSender(LoginController.getLoggedPlayer().getLogin());
		message.addValue("country", country);
		message.addValue("city", city);
		message.addValue("name", name);
		message.addValue("animal", animal);
		client.sendMessage(message);
		return client.getServerResponse();
	}

	public void setAnswers(Message serverResponse) {
		String mapAsString = serverResponse.getValues().get(LoginController.getLoggedPlayer().getLogin());
		String[] answers = mapAsString.substring(1, mapAsString.length()-1).split(",");
		for (String answer : answers) {
			String[] pairs = answer.trim().split("=");
			String key = pairs[0];
			String[] answerValues = pairs[1].split(":");
			String specificAnswer = answerValues[0];

			boolean isCorrect = isCorrectAnswer(answerValues);
			Platform.runLater(() -> {
				if ("country".equalsIgnoreCase(key)) {
					countryImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
															.toExternalForm()));
				} else if ("city".equalsIgnoreCase(key)) {
					cityImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
															.toExternalForm()));
				} else if ("name".equalsIgnoreCase(key)) {
					nameImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
															.toExternalForm()));
				} else if ("animal".equalsIgnoreCase(key)) {
					animalImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
															.toExternalForm()));
				} else if ("points".equalsIgnoreCase(key)) {
					pointsLabel.setText(specificAnswer);
					correctAnswerLabel.setText(specificAnswer + "/4");
				}
			});
		}
	}
	
	private void setOpponentAnswers(Map<String, String> values, Message serverResponse) {
		String mapAsString = serverResponse.getValues().get(values.get("opponent"));
		String[] answers = mapAsString.substring(1, mapAsString.length()-1).split(",");
		for (String answer : answers) {
			String[] pairs = answer.trim().split("=");
			String key = pairs[0];
			String[] answerValues = pairs[1].split(":");
			String specificAnswer = answerValues[0];
			
			boolean isCorrect = isCorrectAnswer(answerValues);
			Platform.runLater(() -> {
				if ("country".equalsIgnoreCase(key)) {
					opponentCountryLabel.setText(specificAnswer);
					opponentCountryImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
																	.toExternalForm()));
				} else if ("city".equalsIgnoreCase(key)) {
					opponentCityLabel.setText(specificAnswer);
					opponentCityImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
																	.toExternalForm()));
				} else if ("name".equalsIgnoreCase(key)) {
					opponentNameLabel.setText(specificAnswer);
					opponentNameImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
																	.toExternalForm()));
				} else if ("animal".equalsIgnoreCase(key)) {
					opponentAnimalLabel.setText(specificAnswer);
					opponentAnimalImg.setImage(new Image(getClass().getResource(isCorrect ? "../view/thumb-up.png" : "../view/thumb-down.png")
																	.toExternalForm()));
				} else if ("points".equalsIgnoreCase(key)) {
					opponentPointsLabel.setText(specificAnswer);
					opponentCorrectAnswerLabel.setText(specificAnswer + "/4");
				}
			});
		}
	}
	
	private void setWinnerImg(Message serverResponse) {
		String winner = serverResponse.getValues().get("winner");
		if(LoginController.getLoggedPlayer().getLogin().equals(winner)) {
			winnerImg.setImage(new Image(getClass().getResource("../view/trophy.png").toExternalForm()));
		} else if("draw".equals(winner)){
			winnerImg.setImage(new Image(getClass().getResource("../view/trophy.png").toExternalForm()));
			opponentWinnerImg.setImage(new Image(getClass().getResource("../view/trophy.png").toExternalForm()));
		} else {
			opponentWinnerImg.setImage(new Image(getClass().getResource("../view/trophy.png").toExternalForm()));
		}
	}
	
	private boolean isCorrectAnswer(String[] answerValues) {
		boolean isCorrect = false;
		if (answerValues.length > 1)
			isCorrect = Boolean.parseBoolean(answerValues[1]);
		return isCorrect;
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
	
	private void waitUntil(BooleanSupplier booleanSupplier) {
		while (booleanSupplier.getAsBoolean()) {
			try {
				Thread.sleep(100); // TODO better solution - wait(100)? not work becouse of exception
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setDisableTextFields(boolean disbale) {
		countryTextField.setDisable(disbale);
		cityTextField.setDisable(disbale);
		nameTextField.setDisable(disbale);
		animalTextField.setDisable(disbale);
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
		newGameBtn.setDisable(false);
		playerAccounBtn.setDisable(false);
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
