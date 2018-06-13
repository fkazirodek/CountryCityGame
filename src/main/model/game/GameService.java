package model.game;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

import exceptions.DuplicateKeyException;
import exceptions.PlayerNotFoundException;
import model.message.Message;
import model.message.OperationType;
import model.player.Player;
import model.player.PlayerService;
import model.words.Animal;
import model.words.City;
import model.words.Country;
import model.words.Name;
import model.words.Word;
import model.words.WordService;
import server.GameProtocol;

/**
 * This class is responsible for managing the state of the game, checking
 * results and generating responses which contains game results. Class also
 * allows login player, register new player and report word which is not
 * present in game's dictionary
 * 
 * @author Filip
 *
 */
public class GameService {

	private static final Set<Game> games = ConcurrentHashMap.newKeySet();
	private WordService wordService;
	private PlayerService playerService;

	public GameService(WordService wordService, PlayerService playerService) {
		this.wordService = wordService;
		this.playerService = playerService;
	}

	public static Set<Game> getGames() {
		return games;
	}
	
	/**
	 * Creates a new game or looks for a game where the player waits for the
	 * opponent and joins it. The game may start if has 2 players.
	 * @param message
	 *            request message, must contains sender
	 * @return Message which contains recipient, oponents and random letter
	 */
	public Message joinToGame(Message message) {
		String player = message.getSender();
		Game game = findActiveGameOrCreateNew(player);

		waitUntil(() -> game.getPlayerSize() < 2);
		return createResponseWithGame(game, player);
	}

	/**
	 * This method checks the sent words, counts the collected points and indicates
	 * the winner
	 * @param message
	 *            must contains sender and words to validate
	 * @return Message which contains game results
	 */
	public Message checkWordsAndGetWinner(Message message) {
		Map<String, String> values = message.getValues();
		String sender = message.getSender();
		List<Word> listOfWords = getListOfWords(values);

		Game activeGame = getGameWithSpecificPlayer(sender);
		long points = calculatePoints(activeGame.getLetter(), listOfWords);
		
		playerService.addPoints((int)points, sender);
		
		Result result = createResult(sender, points, activeGame.getLetter(), listOfWords);
		activeGame.addResult(sender, result);
		
		waitUntil(() -> !activeGame.isFinished());

		games.remove(activeGame);
		return createResponseWithResults(sender, activeGame);
	}

	/**
	 * Save word reported by the player
	 * @param message
	 *            request message
	 * @param values
	 *            Map<String, String> with reported word
	 * @return response message
	 */
	public Message reportWord(Message message, Map<String, String> values) {
		boolean isReported = false;
		String word = values.get("word");
		long player_id = playerService.getPlayer(message.getSender()).getId();
		isReported = wordService.saveReportedWord(word, player_id);
		message = new Message(OperationType.REPORT);
		message.addValue("reported", String.valueOf(isReported));
		return message;
	}
	
	/**
	 * Retrive player
	 * @param values
	 *            Map<String, String> with player's login
	 * @return response message with player or error message if player not found
	 */
	public Message getPlayer(Map<String, String> values) {
		Message message;
		Player player = null;
		try {
			player = playerService.getPlayer(values.get("login"));
		} catch (PlayerNotFoundException e) {
			message = new Message(OperationType.ERROR);
			message.addValue("error", "player not found");
			return message;
		}
		message = new Message(OperationType.OK);
		message.addValue("login", player.getLogin());
		message.addValue("points", String.valueOf(player.getPoints()));
		return message;
	}
	
	/**
	 * Retrive top players
	 * @param limit of players to get
	 * @return response message with top players
	 */
	public Message getPlayers(int limit) {
		List<Player> players = playerService.getAllPlayersLimit(limit);
		Message message = new Message(OperationType.RESULTS);
		players.forEach(p -> {
			message.addValue(p.getLogin(), String.valueOf(p.getPoints()));
		});
		return message;
	}

	/**
	 * Login player into game
	 * @param out
	 * @param values
	 *            Map<String, String> of values with login and password
	 * @return response message
	 */
	public Message login(PrintWriter out, Map<String, String> values) {
		Message message;
		String login = values.get("login");
		String password = values.get("password");
		boolean isLogged = false;
		try {
			isLogged = playerService.loginPlayer(login, password);
		} catch (PlayerNotFoundException e) {
			Message errorMsg = new Message(OperationType.NOT_FOUND);
			errorMsg.addValue("error", e.getMessage());
		}
		if(isLogged)
			GameProtocol.getActiveClients().put(login, out);
		message = new Message(OperationType.LOGIN);
		message.addValue("login", String.valueOf(isLogged));
		return message;
	}

	/**
	 * Register new Player to game
	 * @param values
	 *            Map<String, String> of values with login and password
	 * @return response message
	 */
	public Message register(Map<String, String> values) {
		Message message;
		String login = values.get("login");
		String password = values.get("password");
		boolean isRegister = false;
		try {
			isRegister = playerService.registerPlayer(login, password);
		} catch (DuplicateKeyException e) {
			message = new Message(OperationType.ERROR);
			message.addValue("error", "Gracz o tej nazwie istnieje\n wybierz inny login");
			return message;
		}
		message = new Message(OperationType.REGISTER);
		message.addValue("register", String.valueOf(isRegister));
		return message;
	}

	private Game getGameWithSpecificPlayer(String player) {
		return games.stream()
					.filter(g -> g.getPlayer(player) != null)
					.findFirst()
					.get();
	}
	
	private synchronized void waitUntil(BooleanSupplier action) {
		while (action.getAsBoolean()) {
			try {
				wait(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private synchronized Game findActiveGameOrCreateNew(String player) {
		Game game;
		if (games.isEmpty()) {
			game = new Game();
			game.addPlayer(player);
			games.add(game);
		} else {
			game = games.stream()
						.filter(g -> g.getPlayerSize() == 1)
						.findFirst()
						.orElseGet(() -> {
							Game g = new Game();
							games.add(g);
							return g;
						});
			game.addPlayer(player);
		}
		notifyAll();
		return game;
	}

	private List<Word> getListOfWords(Map<String, String> values) {
		List<Word> listOfWords = new ArrayList<>();
		Country country = new Country(values.get("country"));
		City city = new City(values.get("city"));
		Name name = new Name(values.get("name"));
		Animal animal = new Animal(values.get("animal"));
		listOfWords.add(country);
		listOfWords.add(city);
		listOfWords.add(name);
		listOfWords.add(animal);
		return listOfWords;
	}

	private long calculatePoints(Character letter, List<Word> words) {
		return words
				.stream()
				.filter(word -> wordService.validateWord(word, letter.toString()))
				.count();
	}
	
	private Result createResult(String player, Long points, Character letter, List<Word> words) {
		Result result = new Result();
		result.setPlayer(player);
		result.addValue("points", String.valueOf(points));
		words.forEach(w -> {
			if(w.getValue() != null)
				result.addValue(w.getClass().getSimpleName(), 
								w.getValue() + ":" +
								String.valueOf(wordService.validateWord(w, letter.toString()))
								);
		});
		return result;
	}
	
	private Message createResponseWithGame(Game game, String player) {
		Message response = null;
		if (game.getPlayerSize() == 2) {
			response = new Message(OperationType.GAME);
			String player1 = game.getPlayer(0);
			String player2 = game.getPlayer(1);
			response.addValue("letter", game.getLetter().toString());
			if (player1.equals(player)) {
				response.addValue("opponent", player2);
				response.addRecipient(player1);
			} else if (player2.equals(player)) {
				response.addValue("opponent", player1);
				response.addRecipient(player2);
			}
		}
		return response;
	}
	
	private Message createResponseWithResults(String sender, Game activeGame) {
		String player1 = activeGame.getPlayer(0);
		String player2 = activeGame.getPlayer(1);

		Message response = new Message(OperationType.RESULTS);
		if (sender.equals(player1))
			response.addRecipient(player2);
		else
			response.addRecipient(player1);

		activeGame.getEntrySet().stream().forEach(e -> {
			response.addValue(e.getKey(), e.getValue().getResults().toString());
		});
		
		response.addValue("winner", activeGame.getWinner());
		return response;
	}
	
}
