package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

import database.MySQLConnector;
import model.message.Message;
import model.message.OperationType;
import model.player.PlayerService;
import model.player.PlayerRepository;
import model.words.DataType;
import model.words.Dictionary;
import model.words.WordRepository;
import model.words.WordService;
import utils.DataReader;

/**
 * Game server which is responsible for communication between the client and the
 * server and between the users. Communication with the server is carried out by
 * messages.
 * 
 * @author Filip
 *
 */
public class SocketServer {

	private static PlayerService playerService;
	private static WordService wordService;
	private static GameProtocol gameProtocol;

	public static void main(String[] args) {
		try {
			initData();
			initServer();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void initServer() throws ClassNotFoundException {
		int portNumber = 4444;
		boolean listening = true;

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (listening) {
				Socket socket = serverSocket.accept();
				new Thread(() -> {
					try {
						listenClients(socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
		}
	}

	private static void listenClients(Socket socket) throws IOException {
		
		try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

			String inputLine;
			gameProtocol = new GameProtocol(playerService, wordService);
			initConversation(out);

			while ((inputLine = in.readLine()) != null) {
				sendResponse(out, inputLine);
			}
			socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void initConversation(PrintWriter out) throws InterruptedException {
		Message message = gameProtocol.processInput(new Message(OperationType.START), out);
		out.println(convertToJsonString(message));
	}

	private static void sendResponse(PrintWriter out, String inputLine) {
		Message message = gameProtocol.processInput(convertFromJson(inputLine, Message.class), out);
		String msgJson = convertToJsonString(message);
		
		if (message.getRecipientsSize() == 0) {
			out.println(msgJson);
			
		} else if(message.getRecipientsSize() == 1) {
			PrintWriter writer = gameProtocol
									.getActiveClients()
									.get(message.getRecipient(0));
			
			if (writer != null)
				writer.println(msgJson);
			else 
				out.println(msgJson);
			
		} else if(message.getRecipientsSize() >= 2) {
			message.getRecipients().forEach(rec -> {
				gameProtocol
					.getActiveClients()
					.get(rec)
					.println(msgJson);
			});
		}
	}
	
	private static String convertToJsonString(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	private static <T> T convertFromJson(String s, Class<T> t) {
		Gson gson = new Gson();
		return gson.fromJson(s, t);
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
