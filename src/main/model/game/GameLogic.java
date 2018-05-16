package model.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import model.message.Message;
import model.message.OperationType;
import model.words.Animal;
import model.words.City;
import model.words.Country;
import model.words.Dictionary;
import model.words.Name;
import model.words.Word;

/**
 * This class is responsible for managing the state of the game, checking results and generating responses which contains game results.
 * @author Filip
 *
 */
public class GameLogic {

	private static Set<Game> games = new HashSet<>();
	private Dictionary dictionary;

	public GameLogic(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Creates a new game or looks for a game where the player waits for the opponent and joins it. 
	 * The game may start if has 2 players.
	 * @param message must constains sender
	 * @return Message which contains recipient, oponents and random letter
	 */
	public Message joinToGame(Message message) {
		Game game;
		String player = message.getSender();
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

		while (game.getPlayerSize() < 2) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		if (game.getPlayerSize() == 2) {
			message = new Message(OperationType.GAME);
			String player1 = game.getPlayer(0);
			String player2 = game.getPlayer(1);
			message.addValue("letter", game.getLetter().toString());
			if (player1.equals(player)) {
				message.addValue("opponent", player2);
				message.addRecipient(player1);
			} else if (player2.equals(player)) {
				message.addValue("opponent", player1);
				message.addRecipient(player2);
			}
		}
		return message;
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

		Country country = new Country(values.get("country"));
		City city = new City(values.get("city"));
		Name name = new Name(values.get("name"));
		Animal animal = new Animal(values.get("animal"));

		Game activeGame = games.stream().filter(g -> g.getPlayer(sender) != null).findFirst().get();
		long points = calculatePoints(activeGame.getLetter(), country, city, name, animal);
		
		action.accept((int)points, sender);
		
		Result result = createResult(sender, points, activeGame.getLetter(), country, city, name, animal);
		activeGame.addResult(sender, result);

		while (!activeGame.isFinished()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		String player1 = activeGame.getPlayer(0);
		String player2 = activeGame.getPlayer(1);

		Message msg = new Message(OperationType.RESULTS);
		if (sender.equals(player1))
			message.addRecipient(player2);
		else
			message.addRecipient(player1);

		activeGame.getEntrySet().stream().forEach(e -> {
			msg.addValue(e.getKey(), e.getValue().getResults().toString());
		});
		
		msg.addValue("winner", activeGame.getWinner());
		games.remove(activeGame);
		return msg;
	}

	private boolean isWordCorrect(Word word, String letter) {
		return dictionary.isWordExist(word) && word.getValue().toUpperCase().startsWith(letter);
	}
	
	private Result createResult(String player, Long points, Character letter, Word... words) {
		Result result = new Result();
		result.setPlayer(player);
		result.addValue("points", String.valueOf(points));
		Arrays.stream(words).forEach(w -> {
			result.addValue(w.getValue(), String.valueOf(isWordCorrect(w, letter.toString())));
		});
		return result;
	}

	private long calculatePoints(Character letter, Word... words) {
		return Arrays.stream(words)
						.filter(word -> isWordCorrect(word, letter.toString()))
						.count();
	}
	
}
