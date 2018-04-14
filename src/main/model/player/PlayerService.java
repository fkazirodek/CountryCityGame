package model.player;

import exceptions.DuplicateKeyException;
import exceptions.PlayerNotFoundException;

/**
 * Service class
 * @author Filip
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
	
	/* TODO
	 * - add delete method
	 * - add find all method
	 * - test service
	 */
	
}
