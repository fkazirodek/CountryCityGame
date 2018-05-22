package model.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.message.Message;
import model.player.PlayerService;
import model.words.WordService;

public class GameServiceTest {
	
	private static final String SENDER_1 = "ala";
	private static final String SENDER_2 = "jola";
	private ExecutorService executorService;
	
	private GameService gameService;
	@Mock
	private WordService wordService;
	@Mock
	private PlayerService playerService;
	
	private Message message_1;
	private Message message_2;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameService(wordService, playerService);
		executorService = Executors.newCachedThreadPool();
		message_1 = new Message();
		message_2 = new Message();
	}
	
	@Test
	public void joinToGame() throws ExecutionException, InterruptedException {
		final String key = "opponent";
		message_1.setSender(SENDER_1);
		message_2.setSender(SENDER_2);

		Future<Message> future_1 = executorService.submit(() -> gameService.joinToGame(message_1));
		Future<Message> future_2 = executorService.submit(() -> gameService.joinToGame(message_2));
		
		Message response_1 = future_1.get();
		Message response_2 = future_2.get();
		assertNotNull(response_1);
		assertNotNull(response_2);
		assertEquals(SENDER_2, response_1.getValues().get(key));
		assertEquals(SENDER_1, response_2.getValues().get(key));
	}
}
