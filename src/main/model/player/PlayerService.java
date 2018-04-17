package model.player;

import java.util.List;

import exceptions.DuplicateKeyException;
import exceptions.PlayerNotFoundException;

/**
 * Service class
 * @author Filip K.
 *
 */
public class PlayerService {

	private Repository repository;
	
	public PlayerService(Repository repository) {
		this.repository = repository;
	}
	
	/**
	 * Method checks that the player exists and the password is correct
	 * @param login
	 * 			must not be null
	 * @param password
	 * 			must not be null
	 * @return
	 * 		true if player exist and password is correct, false otherwise
	 * @throws
	 * 		PlayerNotFoundException
	 */
	public boolean loginPlayer(String login, String password) {
		Player player = repository
							.findPlayer(login)
							.orElseThrow(() -> new PlayerNotFoundException());
		return player.getPassword().equals(password) ? true : false;
	}

	/**
	 * Save player in database
	 * @param login
	 * 			must not be null
	 * @param password
	 * 			must not be null
	 * @return
	 * 		true if player has been successfully register, false otherwise
	 * @throws DuplicateKeyException
	 */
	public boolean registerPlayer(String login, String password) throws DuplicateKeyException {
		Player player = new Player(login, password);
		return repository.savePlayer(player);
	}
	
	/**
	 * Add current points to all points
	 * @param points
	 * @param login
	 * @return
	 * 		true if points has been add, false otherwise
	 */
	public boolean addPoints(int points, String login) {
		return repository.updatePoints(points, login);
	}
	
	/**
	 * Get player by login
	 * @param login
	 * @return
	 * 		Player
	 * @throws PlayerNotFoundException
	 */
	public Player getPlayer(String login) {
		return repository
				.findPlayer(login)
				.orElseThrow(() -> new PlayerNotFoundException());
	}
	
	/**
	 * Get all players where the number of players is determined by the limit.
	 * List of players is ordered by points desc.
	 * @param limit
	 * @return
	 * 		List of players, may be empty
	 * 		
	 */
	public List<Player> getAllPlayersLimit(int limit) {
		return repository.findAll(limit);
	}
	
	/**
	 * Delete player
	 * @param login
	 * 			must not be null
	 */
	public void deletePlayer(String login) {
		repository.deletePlayer(login);
	}
}
