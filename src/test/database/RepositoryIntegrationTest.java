package database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exceptions.DuplicateKeyException;
import model.Player;

public class RepositoryIntegrationTest {

	private static final String LOGIN = "player1";
	private static final String PASSWORD = "password";

	private Repository repository;
	
	private Player player;
	
	@Before
	public void before() {
		repository = new Repository(new MySQLConnector());
		player = new Player(LOGIN, PASSWORD, 0);
	}
	
	@After
	public void after() {
		repository.clean();
	}
	
	@Test
	public void playerNotFoundIfWrongLogin() {
		Optional<Player> playerOpt = repository.findPlayer("WrongLogin");
		assertFalse(playerOpt.isPresent());
	}
	
	@Test
	public void shouldAddNewPlayer() {
		boolean isSaved = repository.savePlayer(player);
		assertTrue(isSaved);
	}
	
	@Test
	public void cannotAddIfPlayerNull() {
		boolean isSaved = repository.savePlayer(null);
		assertFalse(isSaved);
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void throwsExceptionIfDuplicateLogin() {
		repository.savePlayer(player);
		repository.savePlayer(player);
	}
	
	@Test
	public void shouldUpdatePoints() {
		boolean isUpdated = false;
		int points = 10;
		
		repository.savePlayer(player);
		isUpdated = repository.updatePoints(points, LOGIN);
		Optional<Player> playerOpt = repository.findPlayer(LOGIN);
		
		assertTrue(playerOpt.isPresent());
		assertEquals(points, playerOpt.get().getPoints());
		assertTrue(isUpdated);
	}
	
	
	@Test
	public void cannotUpdatePointsIfNegative() {
		boolean isUpdated = false;
		
		repository.savePlayer(player);
		isUpdated = repository.updatePoints(-1, LOGIN);
		
		assertFalse(isUpdated);
	}
	
	@Test
	public void shouldDeletePlayer() {
		repository.savePlayer(player);
		repository.deletePlayer(LOGIN);
		Optional<Player> playerOpt = repository.findPlayer(LOGIN);
		
		assertFalse(playerOpt.isPresent());
	}
	
}
