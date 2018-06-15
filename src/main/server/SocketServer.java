package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import model.message.Message;
import model.message.OperationType;

/**
 * Game server which is responsible for communication between the client and the
 * server and between the users. Communication with the server is carried out by
 * messages.
 * 
 * @author Filip
 *
 */
public class SocketServer {

	private static GameProtocol gameProtocol;
	private static Executor executor;

	public static void main(String[] args) {
		try {
			executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			gameProtocol = new GameProtocol();
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
				executor.execute(() -> {
					try {
						ioProcessing(socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
		}
	}

	private static void ioProcessing(Socket socket) throws IOException {
		
		try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

			String inputLine;
			initConversation(out);

			while ((inputLine = in.readLine()) != null) {
				String inputMsg = inputLine;
				executor.execute(() -> sendResponse(out, inputMsg));
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
			PrintWriter writer = GameProtocol
									.getActiveClients()
									.get(message.getRecipient(0));
			
			if (writer != null)
				writer.println(msgJson);
			else 
				out.println(msgJson);
			
		} else if(message.getRecipientsSize() >= 2) {
			message.getRecipients().forEach(rec -> {
				GameProtocol
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

	
}
