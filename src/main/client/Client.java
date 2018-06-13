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

public class Client {
	private static final String HOST_NAME = "127.0.0.2";
	private static final int PORT_NUMBER = 4444;
	
	private volatile static Message request;
	private static Queue<Message> responses = new ConcurrentLinkedQueue<>();

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
				fromUser = gson.toJson(request);
				out.println(fromUser);
				request = null;
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

	private synchronized void waitUntil() throws InterruptedException {
		notifyAll();
		while (request == null) {
			wait();
		}
	}

	public synchronized void sendMessage(Message message) {
		request = message;
		notifyAll();
	}

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

}
