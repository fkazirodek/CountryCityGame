package model.word;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.MySQLConnector;
import model.player.Player;
import model.player.PlayerRepository;
import model.words.WordRepository;

public class WordRepositoryIntegrationTest {
	
	private static final String WORD_NAME = "Ala";
	private static final String LOGIN = "player1";
	
	private PlayerRepository playerRepository;
	private WordRepository wordRepository;
	
	private Player player;
	
	@Before
	public void init() {
		MySQLConnector connector = MySQLConnector.getInstance();
		playerRepository = new PlayerRepository(connector);
		wordRepository = new WordRepository(connector);
		
		player = new Player(LOGIN, "1234");
	}
	
	@After
	public void clean() {
		playerRepository.clean();
	}
	
	@Test
	public void reportWord() {
		playerRepository.savePlayer(player);
		long id = playerRepository.findPlayer(LOGIN)
									.get()
									.getId();
		
		assertTrue(wordRepository.saveReportedWord(WORD_NAME, id));
	}
}
