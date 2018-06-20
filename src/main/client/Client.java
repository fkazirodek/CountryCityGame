package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;

import model.message.Message;

/**
 * The class responsible for communication with the server, in particular for sending and receiving messages 
 * and managing the queue of messages
 * @author 
 *
 */
public class Client {
	
	private static final String HOST_NAME = "127.0.0.1";
	private static final int PORT_NUMBER = 4444;
	
	private static Queue<Message> requests = new ConcurrentLinkedQueue<>();
	private static Queue<Message> responses = new ConcurrentLinkedQueue<>();

	/**
	 * This method starts connections and communication with the server
	 */
	public void start() {
		try (Socket clientSocket = new Socket(HOST_NAME, PORT_NUMBER);
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

			String fromServer;
			String fromUser;

			while ((fromServer = in.readLine()) != null) {
				Gson gson = new Gson();
				responses.add(gson.fromJson(fromServer, Message.class));
				waitUntil();
				fromUser = gson.toJson(requests.poll());
				out.println(fromUser);
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + HOST_NAME);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + HOST_NAME);
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send message to the server
	 * @param message
	 */
	public synchronized void sendMessage(Message message) {
		requests.add(message);
		notifyAll();
	}

	/**
	 * Get message from the server
	 * @return server response message 
	 */
	public synchronized Message getServerResponse() {
		while (responses.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return responses.poll();
	}
	
	private synchronized void waitUntil() throws InterruptedException {
		notifyAll();
		while (requests.isEmpty()) {
			wait();
		}
	}

}
