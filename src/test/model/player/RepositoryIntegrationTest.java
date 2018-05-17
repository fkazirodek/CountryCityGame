package model.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.MySQLConnector;
import exceptions.DuplicateKeyException;
import model.player.Player;
import model.player.PlayerRepository;

public class RepositoryIntegrationTest {

	private static final String LOGIN = "player1";
	private static final String PASSWORD = "password";

	private PlayerRepository playerRepository;
	
	private Player player;
	
	@Before
	public void before() {
		playerRepository = new PlayerRepository(MySQLConnector.getInstance());
		player = new Player(LOGIN, PASSWORD, 0);
	}
	
	@After
	public void after() {
		playerRepository.clean();
	}
	
	@Test
	public void findAllInOrder() {
		Player playerN1 = new Player("login1", "pass", 0);
		Player playerN2 = new Player("login2", "pass", 0);
		playerRepository.savePlayer(player);
		playerRepository.savePlayer(playerN2);
		playerRepository.savePlayer(playerN1);
		
		playerRepository.updatePoints(999, "login1");
		
		List<Player> players = playerRepository.findAll(2);
		
		assertEquals(2, players.size());
		assertEquals(playerN1, players.get(0));
	}
	
	@Test
	public void playerNotFoundIfWrongLogin() {
		Optional<Player> playerOpt = playerRepository.findPlayer("WrongLogin");
		assertFalse(playerOpt.isPresent());
	}
	
	@Test
	public void shouldAddNewPlayer() {
		boolean isSaved = playerRepository.savePlayer(player);
		Optional<Player> playerOpt = playerRepository.findPlayer(LOGIN);
		
		assertTrue(isSaved);
		assertTrue(playerOpt.isPresent());
		assertEquals(player, playerOpt.get());
	}
	
	@Test
	public void cannotAddIfPlayerNull() {
		boolean isSaved = playerRepository.savePlayer(null);
		assertFalse(isSaved);
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void throwsExceptionIfDuplicateLogin() {
		playerRepository.savePlayer(player);
		playerRepository.savePlayer(player);
	}
	
	@Test
	public void shouldUpdatePoints() {
		boolean isUpdated = false;
		int points = 10;
		
		playerRepository.savePlayer(player);
		isUpdated = playerRepository.updatePoints(points, LOGIN);
		Optional<Player> playerOpt = playerRepository.findPlayer(LOGIN);
		
		assertTrue(playerOpt.isPresent());
		assertEquals(points, playerOpt.get().getPoints());
		assertTrue(isUpdated);
	}
	
	
	@Test
	public void cannotUpdatePointsIfNegative() {
		boolean isUpdated = false;
		
		playerRepository.savePlayer(player);
		isUpdated = playerRepository.updatePoints(-1, LOGIN);
		
		assertFalse(isUpdated);
	}
	
	@Test
	public void shouldDeletePlayer() {
		playerRepository.savePlayer(player);
		playerRepository.deletePlayer(LOGIN);
		Optional<Player> playerOpt = playerRepository.findPlayer(LOGIN);
		
		assertFalse(playerOpt.isPresent());
	}
	
}
