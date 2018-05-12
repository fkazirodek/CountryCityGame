package server;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import exceptions.DuplicateKeyException;
import exceptions.PlayerNotFoundException;
import model.game.GameLogic;
import model.message.Message;
import model.player.Player;
import model.player.PlayerService;
import model.words.Dictionary;

public class GameProtocol {

	private PlayerService playerService;
	private GameLogic gameLogic;

	private static Map<String, PrintWriter> activeClients = new HashMap<>();
	
	public GameProtocol(PlayerService playerService, Dictionary dictionary) {
		this.playerService = playerService;
		gameLogic = new GameLogic(dictionary);
	}
	
	public Map<String, PrintWriter> getActiveClients() {
		return activeClients;
	}

	public Message processInput(Message message, PrintWriter out) {
		Map<String, String> values = message.getValues();
		switch (message.getOperation()) {
		case "START":
			message =  new Message();
			message.getValues().put("Server", "Hello");
			return message;
		case "REGISTER":
			return register(values);
		case "LOGIN":
			return login(out, values);
		case "GET":
			return getPlayer(values);
		case "PLAY":
			return gameLogic.joinToGame(message);
		case "WORDS":
			return gameLogic.checkWordsAndGetWinner(message, playerService::addPoints);
		}
		return new Message("BAD_REQUEST");
	}

	private Message getPlayer(Map<String, String> values) {
		Message message;
		Player player = null;
		try {
			player = playerService.getPlayer(values.get("login"));
		} catch (PlayerNotFoundException e) {
			message = new Message("NOT_FOUNT");
			message.addValue("error", "player not found");
			return message;
		}
		message = new Message("OK");
		message.addValue("login", player.getLogin());
		message.addValue("points", String.valueOf(player.getPoints()));
		return message;
				
	}

	private Message login(PrintWriter out, Map<String, String> values) {
		Message message;
		String login = values.get("login");
		String password = values.get("password");
		boolean isLogged = playerService.loginPlayer(login, password);
		if(isLogged)
			activeClients.put(login, out);
		message = new Message("LOGGED");
		message.addValue("logged", String.valueOf(isLogged));
		return message;
	}

	private Message register(Map<String, String> values) {
		Message message;
		String login = values.get("login");
		String password = values.get("password");
		boolean isRegister = false;
		try {
			isRegister = playerService.registerPlayer(login, password);
		} catch (DuplicateKeyException e) {
			message = new Message("ERROR");
			message.addValue("error", "Duplicate login");
			return message;
		}
		message = new Message("REGISTER");
		message.addValue("registered", String.valueOf(isRegister));
		return message;
	}
	
}