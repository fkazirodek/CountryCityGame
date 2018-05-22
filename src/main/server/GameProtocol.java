package server;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import database.MySQLConnector;
import model.game.GameService;
import model.message.Message;
import model.message.OperationType;
import model.player.PlayerRepository;
import model.player.PlayerService;
import model.words.DataType;
import model.words.Dictionary;
import model.words.WordRepository;
import model.words.WordService;
import utils.DataReader;

/**
 * Class contains methods that allows process the messages, generate responses,
 * and initiates data needed for the game (eg dictionary)
 * 
 * @author Filip
 */
public class GameProtocol {

	private static final Map<String, PrintWriter> activeClients = new ConcurrentHashMap<>();
	
	private static GameService gameService;
	private static PlayerService playerService;
	private static WordService wordService;
	
	public GameProtocol() {
		initData();
		gameService = new GameService(wordService, playerService);
	}
	
	public static Map<String, PrintWriter> getActiveClients() {
		return activeClients;
	}

	/**
	 * This method is responsible for processing the received message. Message must
	 * contains Map<String, String> of values (eg login, password, words etc)
	 * 
	 * @param message
	 *            from the client
	 * @param out
	 *            client's text-output stream (PrintWriter)
	 * @return Response message
	 */
	public Message processInput(Message message, PrintWriter out) {
		Map<String, String> values = message.getValues();
		switch (message.getOperation()) {
		case START:
			message = new Message();
			message.addValue("Server", "Hello");
			return message;
		case REGISTER:
			return gameService.register(values);
		case LOGIN:
			return gameService.login(out, values);
		case LOGOUT:
			activeClients.remove(message.getSender());
			message = new Message(OperationType.LOGOUT);
			return message;
		case GET_USER:
			return gameService.getPlayer(values);
		case PLAY:
			return gameService.joinToGame(message);
		case WORDS:
			return gameService.checkWordsAndGetWinner(message);
		case REPORT:
			return gameService.reportWord(message, values);
		default:
			return new Message(OperationType.ERROR);
		}
	}
	
	private static void initData() {
		PlayerRepository playerRepository = new PlayerRepository(MySQLConnector.getInstance());
		WordRepository wordRepository = new WordRepository(MySQLConnector.getInstance());
		Dictionary dictionary = new Dictionary();
		readFile(dictionary);
		playerService = new PlayerService(playerRepository);
		wordService = new WordService(dictionary, wordRepository);
		
	}

	private static void readFile(Dictionary dictionary) {
		DataReader dataReader = new DataReader(dictionary);
		dataReader.readDataFromFile("resources/Countries.txt", DataType.COUNTRY);
		dataReader.readDataFromFile("resources/Cities.txt", DataType.CITY);
		dataReader.readDataFromFile("resources/Names.txt", DataType.NAME);
	}
	
}