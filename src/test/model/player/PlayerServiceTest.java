package model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import exceptions.PlayerNotFoundException;

public class PlayerServiceTest {

	private static final String LOGIN = "player";
	private static final String PASSWORD = "password";
	
	@Mock
	private PlayerRepository playerRepository;
	
	private PlayerService playerService;
	
	private Player player;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		playerService = new PlayerService(playerRepository);
		
		player = new Player(LOGIN, PASSWORD, 0);
	}
	
	@Test
	public void findPlayer() {
		when(playerRepository.findPlayer(LOGIN)).thenReturn(Optional.of(player));
		
		Player playerDB = playerService.getPlayer(LOGIN);
		
		assertEquals(player, playerDB);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void throwsExceptionWhenPlayerNotFound() {
		when(playerRepository.findPlayer(LOGIN)).thenReturn(Optional.empty());
		playerService.getPlayer(LOGIN);
	}
	
	@Test
	public void findAllPlayers() {
		when(playerRepository.findAll(2)).thenReturn(Collections.singletonList(new Player(LOGIN)));
		
		List<Player> players = playerService.getAllPlayersLimit(2);
		
		assertFalse(players.isEmpty());
	}
	
	@Test
	public void loginPlayerSuccessful() {
		when(playerRepository.findPlayer(LOGIN)).thenReturn(Optional.of(player));
		
		boolean isLogged = playerService.loginPlayer(LOGIN, PASSWORD);
		
		assertTrue(isLogged);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void loginFailWhenWrongLogin() {
		String wrongLogin = "wrong";
		when(playerRepository.findPlayer(wrongLogin)).thenReturn(Optional.empty());
		playerService.loginPlayer(wrongLogin, PASSWORD);
	}
	
	@Test
	public void loginFailWhenWrongPassword() {
		when(playerRepository.findPlayer(LOGIN)).thenReturn(Optional.of(player));
		
		boolean isLogged = playerService.loginPlayer(LOGIN, "wrong password");
		
		assertFalse(isLogged);
	}
	
	@Test
	public void registerPlayer() {
		when(playerRepository.savePlayer(player)).thenReturn(true);
		
		boolean isRegistered = playerService.registerPlayer(LOGIN, PASSWORD);
		
		assertTrue(isRegistered);
	}
	
	@Test
	public void addPoints() {
		when(playerRepository.updatePoints(10, LOGIN)).thenReturn(true);
		
		boolean isAdded = playerService.addPoints(10, LOGIN);
		
		assertTrue(isAdded);
	}
	
	@Test
	public void addNegativPoints() {
		when(playerRepository.updatePoints(-10, LOGIN)).thenReturn(false);
		
		boolean isAdded = playerService.addPoints(-10, LOGIN);
		
		assertFalse(isAdded);
	}

}
