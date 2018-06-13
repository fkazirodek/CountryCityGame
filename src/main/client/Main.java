package client;
	
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controller.LoginController;
import controller.MainController;
import controller.RegisterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	
	private ExecutorService executorService;
	private Client client;
	private Stage primaryStage;
	private Stage loginStage;
	private Stage registerStage;
	private Stage waitingStage;
	
	private MainController mainController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		client = new Client();
		executorService.execute(client::start);
		boolean isConnected = Boolean.parseBoolean(client.getServerResponse()
															.getValues()
															.get("connected"));
		if(isConnected) {
			initMainWindow();
			showLoginWindow();
			initRegisterWindow();
			initWaitingWindow();
		}
	}
	
	public Client getClient() {
		return client;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	private void initMainWindow() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/MainWindowView.fxml"));
		try {
			BorderPane root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("../view/application.css").toExternalForm());
			
			mainController = fxmlLoader.getController();
			mainController.setMain(this);
			
			primaryStage.setOnHiding(e -> {
				executorService.shutdown();
				primaryStage.close(); 
			});
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showLoginWindow() {
		loginStage = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/LoginWindowView.fxml"));
		try {
			AnchorPane anchorPane = fxmlLoader.load();
			Scene scene = new Scene(anchorPane);
			
			LoginController loginController = fxmlLoader.getController();
			loginController.setMain(this);
			
			loginStage.setOnHiding(e -> mainController.initData());
			
			loginStage.setScene(scene);
			loginStage.initOwner(primaryStage);
			loginStage.initModality(Modality.APPLICATION_MODAL); 
			loginStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initRegisterWindow() {
		registerStage = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/RegisterWindowView.fxml"));
		try {
			AnchorPane anchorPane = fxmlLoader.load();
			Scene scene = new Scene(anchorPane);
			
			RegisterController registerController = fxmlLoader.getController();
			registerController.setMain(this);
			
			registerStage.setScene(scene);
			registerStage.initOwner(loginStage);
			registerStage.initModality(Modality.APPLICATION_MODAL); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initWaitingWindow() {
		waitingStage = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/WaitingWindowView.fxml"));
		try {
			AnchorPane anchorPane = fxmlLoader.load();
			Scene scene = new Scene(anchorPane);
			
			waitingStage.setScene(scene);
			waitingStage.initOwner(primaryStage);
			waitingStage.initModality(Modality.APPLICATION_MODAL); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showAlert(String title, String textMsg, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(textMsg);
		alert.show();
	}
	
	public void showRegisterWindow() {
		registerStage.showAndWait();
	}
	
	public void showWaitingWindow() {
		waitingStage.showAndWait();
	}
	
	public void hideWaitingWindow() {
		waitingStage.hide();
	}
	
	public void hideLoginWindow() {
		loginStage.hide();
	}
	
	public void hideRegisterWindow() {
		registerStage.hide();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
