package application;
	
import client.Client;
import controller.LoginController;
import controller.MainController;
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
	
	private Client client;
	private Stage primaryStage;
	private Stage loginStage;
	
	private MainController mainController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		client = new Client();
		new Thread(client::start).start();
		boolean isConnected = Boolean.parseBoolean(client.getServerResponse()
															.getValues()
															.get("connected"));
		if(isConnected) {
			initMainWindow();
			showLoginWindow();
		}
	}
	
	public Client getClient() {
		return client;
	}
	
	public void hideLoginWindow() {
		loginStage.hide();
	}
	
	private void initMainWindow() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
		try {
			BorderPane root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("../view/application.css").toExternalForm());
			
			mainController = fxmlLoader.getController();
			mainController.setMain(this);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showLoginWindow() {
		loginStage = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoginWindowView.fxml"));
		try {
			AnchorPane anchorPane = fxmlLoader.load();
			Scene scene = new Scene(anchorPane);
			
			LoginController loginController = fxmlLoader.getController();
			loginController.setMain(this);
			
			loginStage.setOnHiding((e) -> mainController.initData());
			
			loginStage.setScene(scene);
			loginStage.initOwner(primaryStage);
			loginStage.initModality(Modality.APPLICATION_MODAL); 
			loginStage.showAndWait();
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
