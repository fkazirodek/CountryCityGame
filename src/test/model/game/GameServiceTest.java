package model.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.message.Message;
import model.message.OperationType;
import model.player.PlayerService;
import model.words.Word;
import model.words.WordService;

public class GameServiceTest {
	
	private static final String PLAYER_1 = "ala";
	private static final String PLAYER_2 = "jola";
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
		message_1.setSender(PLAYER_1);
		message_2.setSender(PLAYER_2);
	}
	
	@After
	public void clean() {
		GameService.getGames().clear();
	}
	
	@Test
	public void joinToGame() throws ExecutionException, InterruptedException {
		final String key = "opponent";
		
		Future<Message> result_1 = executorService.submit(() -> gameService.joinToGame(message_1));
		Future<Message> result_2 = executorService.submit(() -> gameService.joinToGame(message_2));
		
		Message response_1 = result_1.get();
		Message response_2 = result_2.get();
		
		assertNotNull(response_1);
		assertNotNull(response_2);
		assertEquals(PLAYER_2, response_1.getValues().get(key));
		assertEquals(PLAYER_1, response_2.getValues().get(key));
	}
	
	@Test
	public void morePlayerJoinToGame() throws InterruptedException {
		List<Callable<Message>> msgList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			int id = i;
			msgList.add(() -> {
				Message message = new Message();
				message.setSender("Sender nr " + id);
				return gameService.joinToGame(message);
			});
		}
		executorService.invokeAll(msgList);
		assertEquals(5, GameService.getGames().size());
	}
	
	@Test(expected=TimeoutException.class)
	public void noPlayersJoinedToGame() throws InterruptedException, ExecutionException, TimeoutException {
		Future<Message> result_1 = executorService.submit(() -> gameService.joinToGame(message_1));
		result_1.get(500, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void checkWordsAndGetWinner() throws InterruptedException, ExecutionException {
		message_1.addValue("country", "Polska");
		message_2.addValue("country", "Polska");
		
		Game game = new Game();
		game.addPlayer(PLAYER_1);
		game.addPlayer(PLAYER_2);
		GameService.getGames().add(game);
		
		when(wordService.validateWord(any(Word.class), anyString())).thenReturn(true);
		when(playerService.addPoints(anyInt(), anyString())).thenReturn(true);
		
		Future<Message> result_1 = executorService.submit(() -> gameService.checkWordsAndGetWinner(message_1));
		Future<Message> result_2 = executorService.submit(() -> gameService.checkWordsAndGetWinner(message_2));
		
		Message response_1 = result_1.get();
		Message response_2 = result_2.get();
		
		assertEquals(OperationType.RESULTS, response_1.getOperation());
		assertEquals(PLAYER_2, response_1.getRecipient(0));
		assertEquals(OperationType.RESULTS, response_2.getOperation());
		assertEquals(PLAYER_1, response_2.getRecipient(0));
	}
	
	
	
	
	
	
	
}
