package model.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

import model.message.Message;
import model.message.OperationType;
import model.words.Animal;
import model.words.City;
import model.words.Country;
import model.words.Name;
import model.words.Word;
import model.words.WordService;

/**
 * This class is responsible for managing the state of the game, checking results and generating responses which contains game results.
 * @author Filip
 *
 */
public class GameLogic {

	private static Set<Game> games = new HashSet<>();
	private WordService wordService;

	public GameLogic(WordService wordService) {
		this.wordService = wordService;
	}

	/**
	 * Creates a new game or looks for a game where the player waits for the opponent and joins it. 
	 * The game may start if has 2 players.
	 * @param message must constains sender
	 * @return Message which contains recipient, oponents and random letter
	 */
	public Message joinToGame(Message message) {
		String player = message.getSender();
		Game game = findActiveGameOrCreateNew(player);
		
		waitUntil(() -> game.getPlayerSize() < 2);
		return createResponseWithGame(game, player);
	}

	/**
	 * This method checks the sent words, counts the collected points and indicates the winner
	 * @param message must contains sender and words to validate
	 * @param action should update points for player
	 * @return Message which contains game results
	 */
	public Message checkWordsAndGetWinner(Message message, BiConsumer<Integer, String> action) {
		Map<String, String> values = message.getValues();
		String sender = message.getSender();
		List<Word> listOfWords = getListOfWords(values);

		Game activeGame = getGameWithSpecificPlayer(sender);
		long points = calculatePoints(activeGame.getLetter(), listOfWords);
		
		action.accept((int)points, sender);
		
		Result result = createResult(sender, points, activeGame.getLetter(), listOfWords);
		activeGame.addResult(sender, result);

		waitUntil(() -> !activeGame.isFinished());

		games.remove(activeGame);
		return createResponseWithResults(sender, activeGame);
	}

	private Game getGameWithSpecificPlayer(String player) {
		return games.stream()
					.filter(g -> g.getPlayer(player) != null)
					.findFirst()
					.get();
	}
	
	public void waitUntil(BooleanSupplier action) {
		while (action.getAsBoolean()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private Game findActiveGameOrCreateNew(String player) {
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

	private boolean isWordCorrect(Word word, String letter) {
		return wordService.wordValidate(word) 
				&& word.getValue().toUpperCase().startsWith(letter);
	}

	private long calculatePoints(Character letter, List<Word> words) {
		return words
				.stream()
				.filter(word -> isWordCorrect(word, letter.toString()))
				.count();
	}
	
	private Result createResult(String player, Long points, Character letter, List<Word> words) {
		Result result = new Result();
		result.setPlayer(player);
		result.addValue("points", String.valueOf(points));
		words.forEach(w -> {
			if(w != null)
				result.addValue(w.getValue(), 
								String.valueOf(isWordCorrect(w, letter.toString()))
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
