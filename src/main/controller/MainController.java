package controller;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.BooleanSupplier;

import client.Client;
import client.Main;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.game.Timer;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;

public class MainController {

	private static final String IMAGE_TROPHY_URL = "/view/images/trophy.png";
	private static final String IMAGE_THUMB_DOWN_URL = "/view/images/thumb-down.png";
	private static final String IMAGE_THUMB_UP_URL = "/view/images/thumb-up.png";
	private static final String IMAGE_SILVER_MEDAL_URL = "/view/images/silver-medal.png";
	
	private Client client;
	private Main main;
	private ExecutorService executorService;
	private Timer timer;	
	private ObservableList<Player> players;
	
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
	private ImageView refreshPlayerListImg;
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
	private ImageView reportCountryImg;
	@FXML
	private ImageView reportCityImg;
	@FXML
	private ImageView reportNameImg;
	@FXML
	private ImageView reportAnimalImg;
	@FXML
	private ImageView opponentWinnerImg;
	@FXML
	private TableView<Player> playersTableView;
	@FXML
	private TableColumn<Player, String> loginColumn;
	@FXML
	private TableColumn<Player, Integer> pointsColumn;
	
	public void initialize() {
		timer = new Timer();
		players = FXCollections.observableArrayList();
		bindColumnWithData();
	}
	
	public void initData() {
		client = main.getClient();
		executorService = main.getExecutorService();
		setPlayerWindow();
		setNumberOfLoggedUsers();
		addPlayersToObservableList();
		disableImagesToReportWords(true);
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	@FXML
	public void refreshLoggedUsers() {
		rotateImg(refreshImageView);
		setNumberOfLoggedUsers();
	}
	
	@FXML
	public void refreshPlayerList() {
		rotateImg(refreshPlayerListImg);
		addPlayersToObservableList();
	}
	
	@FXML
	public void newGame() {
		executorService.execute(() -> {
			setVisibleImagesToReportWords(false);
			disableImagesToReportWords(true);
			newGameBtn.setDisable(true);
			Platform.runLater(() -> main.showWaitingWindow());
			
			Message message = new Message(OperationType.NEW_GAME);
			message.setSender(LoginController.getLoggedPlayer().getLogin());
			client.sendMessage(message);
			
			Message response = client.getServerResponse();
			Platform.runLater(() -> main.hideWaitingWindow());
			Map<String, String> values = response.getValues();
			
			disablePlayerFieldsAndBtn(false);
			
			Platform.runLater(() -> {
				clearOpponentWindow();
				clearPlayerWindow();
				opponentLabel.setText(values.get("opponent"));
				letterLabel.setText(values.get("letter"));
			});

			timer.startTimer(timerLabel::setText);
			waitUntil(() -> timer.getCounterValue() >= 1);
			
			Message serverResponse = sendAnswers();
			setAnswers(serverResponse);
			setOpponentAnswers(values, serverResponse);
			setWinnerImg(serverResponse);
			newGameBtn.setDisable(false);
		});
	}
	
	@FXML
	public Message sendAnswers() {
		timer.setNewCounerValue(0);
		disablePlayerFieldsAndBtn(true);
		Message message = createMessageWithAnswers();
		client.sendMessage(message);
		return client.getServerResponse();
	}
	
	@FXML
	public void reportWord(Event e) {
		ImageView imageView = (ImageView) e.getSource();
		String imageViewID = imageView.getId();
		String word = null;
		switch (imageViewID) {
		case "reportCountryImg":
			word = countryTextField.getText();
			reportCountryImg.setDisable(true);
			reportCountryImg.setVisible(false);
			break;
		case "reportCityImg":
			word = cityTextField.getText();
			reportCityImg.setDisable(true);
			reportCityImg.setVisible(false);
			break;
		case "reportNameImg":
			word = nameTextField.getText();
			reportNameImg.setDisable(true);
			reportNameImg.setVisible(false);
			break;
		case "reportAnimalImg":
			word = animalTextField.getText();
			reportAnimalImg.setDisable(true);
			reportAnimalImg.setVisible(false);
			break;
		}

		Message message = new Message(OperationType.REPORT);
		message.setSender(LoginController.getLoggedPlayer().getLogin());
		message.addValue("word", word);
		client.sendMessage(message);
		client.getServerResponse();
	}
	
	private Message createMessageWithAnswers() {
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
		return message;
	}
	
	public void setAnswers(Message serverResponse) {
		String mapAsString = serverResponse.getValues().get(LoginController.getLoggedPlayer().getLogin());
		String[] answers = mapAsString.substring(1, mapAsString.length()-1).split(",");
		for (String answer : answers) {
			String[] pairs = answer.trim().split("=");
			String key = pairs[0];
			String[] answerValues = pairs[1].split(":");
			String value = answerValues[0];
			boolean isCorrect = isCorrectAnswer(answerValues);
			
			Platform.runLater(() -> {
				if ("country".equalsIgnoreCase(key)) {
					countryImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
															.toExternalForm()));
					reportCountryImg.setVisible(isCorrect ? false : true);
					reportCountryImg.setDisable(isCorrect ? true : false);
				} else if ("city".equalsIgnoreCase(key)) {
					cityImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
															.toExternalForm()));
					reportCityImg.setVisible(isCorrect ? false : true);
					reportCityImg.setDisable(isCorrect ? true : false);
				} else if ("name".equalsIgnoreCase(key)) {
					nameImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
															.toExternalForm()));
					reportNameImg.setVisible(isCorrect ? false : true);
					reportNameImg.setDisable(isCorrect ? true : false);
				} else if ("animal".equalsIgnoreCase(key)) {
					animalImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
															.toExternalForm()));
					reportAnimalImg.setVisible(isCorrect ? false : true);
					reportAnimalImg.setDisable(isCorrect ? true : false);
				} else if ("points".equalsIgnoreCase(key)) {
					pointsLabel.setText(value);
					correctAnswerLabel.setText(value + "/4");
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
			String value = answerValues[0];
			boolean isCorrect = isCorrectAnswer(answerValues);
			
			Platform.runLater(() -> {
				if ("country".equalsIgnoreCase(key)) {
					opponentCountryLabel.setText(value);
					opponentCountryImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
																	.toExternalForm()));
				} else if ("city".equalsIgnoreCase(key)) {
					opponentCityLabel.setText(value);
					opponentCityImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
																	.toExternalForm()));
				} else if ("name".equalsIgnoreCase(key)) {
					opponentNameLabel.setText(value);
					opponentNameImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
																	.toExternalForm()));
				} else if ("animal".equalsIgnoreCase(key)) {
					opponentAnimalLabel.setText(value);
					opponentAnimalImg.setImage(new Image(getClass().getResource(isCorrect ? IMAGE_THUMB_UP_URL : IMAGE_THUMB_DOWN_URL)
																	.toExternalForm()));
				} else if ("points".equalsIgnoreCase(key)) {
					opponentPointsLabel.setText(value);
					opponentCorrectAnswerLabel.setText(value + "/4");
				}
			});
		}
	}
	
	
	
	private void setWinnerImg(Message serverResponse) {
		String winner = serverResponse.getValues().get("winner");
		if(LoginController.getLoggedPlayer().getLogin().equals(winner)) {
			winnerImg.setImage(new Image(getClass().getResource(IMAGE_TROPHY_URL)
													.toExternalForm()));
			opponentWinnerImg.setImage(new Image(getClass().getResource(IMAGE_SILVER_MEDAL_URL)
															.toExternalForm()));
		} else if("draw".equals(winner)){
			winnerImg.setImage(new Image(getClass().getResource(IMAGE_SILVER_MEDAL_URL)
													.toExternalForm()));
			opponentWinnerImg.setImage(new Image(getClass().getResource(IMAGE_SILVER_MEDAL_URL)
															.toExternalForm()));
		} else {
			opponentWinnerImg.setImage(new Image(getClass().getResource(IMAGE_TROPHY_URL)
															.toExternalForm()));
			winnerImg.setImage(new Image(getClass().getResource(IMAGE_SILVER_MEDAL_URL)
													.toExternalForm()));
		}
	}
	

	private void rotateImg(ImageView imageView) {
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), imageView);
		rotateTransition.setByAngle(360);
		rotateTransition.play();
	}
	
	private boolean isCorrectAnswer(String[] answerValues) {
		boolean isCorrect = false;
		if (answerValues.length > 1)
			isCorrect = Boolean.parseBoolean(answerValues[1]);
		return isCorrect;
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
	
	private void setPlayerWindow() {
		Player loggedPlayer = LoginController.getLoggedPlayer();
		if (loggedPlayer != null) {
			setLoggedUserWindow(loggedPlayer);
		} else {
			setUnloggedUserWindow();
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
				setUnloggedUserWindow();
				setNumberOfLoggedUsers();
			}
		});
	}

	private void setUnloggedUserWindow() {
		newGameBtn.setDisable(true);
		playerAccounBtn.setDisable(true);
		logoutBtn.setText("Zaloguj");
		welcomeLabel.setText("Niezalogowany");
		clearOpponentWindow();
		clearPlayerWindow();
		clearRightInfoWindow();
		logoutBtn.setOnAction((e) -> main.showLoginWindow());
	}
	
	private void setNumberOfLoggedUsers() {
		Message mesage = new Message(OperationType.GET_NUM_OF_LOGGED_USERS);
		client.sendMessage(mesage);
		Message response = client.getServerResponse();
		int numberOfLoginUsers = Integer.parseInt(response.getValues().get("numLogin"));
		infoLabel.setText("Zalogowanych użytkowników: " + numberOfLoginUsers);
	}
	
	private void disableImagesToReportWords(boolean disable) {
		reportCountryImg.setDisable(disable);
		reportCityImg.setDisable(disable);
		reportNameImg.setDisable(disable);
		reportAnimalImg.setDisable(disable);
	}
	
	private void setVisibleImagesToReportWords(boolean visible) {
		reportCountryImg.setVisible(visible);
		reportCityImg.setVisible(visible);
		reportNameImg.setVisible(visible);
		reportAnimalImg.setVisible(visible);
	}
	
	private void disablePlayerFieldsAndBtn(boolean disable) {
		countryTextField.setDisable(disable);
		cityTextField.setDisable(disable);
		nameTextField.setDisable(disable);
		animalTextField.setDisable(disable);
		sendAnswersBtn.setDisable(disable);
	}

	private void clearPlayerWindow() {
		countryTextField.clear();
		cityTextField.clear();
		nameTextField.clear();
		animalTextField.clear();
		pointsLabel.setText("0");
		correctAnswerLabel.setText("0/4");
		countryImg.setImage(null);
		cityImg.setImage(null);
		nameImg.setImage(null);
		animalImg.setImage(null);
		winnerImg.setImage(null);
	}
	
	private void clearOpponentWindow() {
		opponentCountryLabel.setText("");
		opponentCityLabel.setText("");
		opponentNameLabel.setText("");
		opponentAnimalLabel.setText("");
		opponentPointsLabel.setText("0");
		opponentCorrectAnswerLabel.setText("0/4");
		opponentCountryImg.setImage(null);
		opponentCityImg.setImage(null);
		opponentNameImg.setImage(null);
		opponentAnimalImg.setImage(null);
		opponentWinnerImg.setImage(null);
	}
	
	private void clearRightInfoWindow() {
		timerLabel.setText("00:00");
		letterLabel.setText("-");
		opponentLabel.setText("-");
	}
	
	private void bindColumnWithData() {
		playersTableView.setItems(players);
		loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
		pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
	}
	
	public void addPlayersToObservableList() {
		players.clear();
		Message message = new Message(OperationType.GET_USERS);
		client.sendMessage(message);
		Message serverResponse = client.getServerResponse();
		serverResponse.getValues().entrySet().forEach(e -> {
			Player player = new Player(e.getKey());
			player.setPoints(Integer.parseInt(e.getValue()));
			players.add(player);
		});
		players.sort(Comparator.comparing(Player::getPoints)
								.reversed());
	}
}
