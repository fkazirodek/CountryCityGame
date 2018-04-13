package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import exceptions.DuplicateKeyException;
import model.Player;

/**
 * This repository class allows make CRUD operations on database
 * @author Filip K.
 */
public class Repository {
	
	
	private static final String CREATE_TABLE = "CREATE TABLE `game`.`players` (\r\n" + 
												"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
												"  `login` VARCHAR(45) NOT NULL,\r\n" + 
												"  `password` VARCHAR(45) NULL,\r\n" + 
												"  `points` INT NULL,\r\n" + 
												"  PRIMARY KEY (`id`, `login`),\r\n" + 
												"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
												"  UNIQUE INDEX `login_UNIQUE` (`login` ASC));\r\n";

	private static final String INSERT = "INSERT INTO players(login, password, points) VALUES (?, ?, 0)";
	private static final String SELECT = "SELECT * FROM players WHERE login = ?";
	private static final String UPDATE = "UPDATE players SET points = points + ? WHERE login = ?";
	private static final String DELETE = "DELETE FROM players WHERE login = ?";
	private static final String DELETE_ALL = "DELETE FROM players WHERE id > 0;";
	
	private static final String COLUMN_LOGIN = "login";
	private static final String COLUMN_PASSWORD = "password";
	private static final String COLUMN_POINTS = "points";
	
	private DBConnector connector;
	
	public Repository(DBConnector dbConnector) {
		connector = dbConnector;
	}
	
	/**
	 * This method creates a new table 'players' in the database, should invoked only once
	 */
	private void createTable() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.executeQuery(CREATE_TABLE);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the given Player
	 * @param player
	 * 			must not be null
	 * @return
	 * 		true if Player saved in database, false otherwise
	 */
	public boolean savePlayer(Player player) {
		if(player == null)
			return false;
		boolean isInserted = false;
		
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
			
			preparedStatement.setString(1, player.getLogin());
			preparedStatement.setString(2, player.getPassword());
			isInserted = preparedStatement.executeUpdate() > 0 ? true : false;
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			throw new DuplicateKeyException(e.getMessage());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return isInserted;
	}
	
	/**
	 * Retrieves Player by login
	 * @param login
	 * 			must not be null
	 * @return
	 * 		Optional which holds the value
	 */
	public Optional<Player> findPlayer(String login) {
		Player player = null;
		
		try(Connection connection = connector.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT)) {
			
			preparedStatement.setString(1, login);
			ResultSet resultSet = preparedStatement.executeQuery();
			player = getPlayer(resultSet);
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(player);
	}
	
	/**
	 * Update player points
	 * @param points
	 * 			must be greater than 0
	 * @param login
	 * 			must not be null
	 */
	public boolean updatePoints(int points, String login) {
		if(points < 0 || login == null)
			return false;
		boolean isUpdated = false;
		try(Connection connection = connector.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
			
			preparedStatement.setInt(1, points);
			preparedStatement.setString(2, login);
			isUpdated = preparedStatement.executeUpdate() > 0 ? true : false;
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return isUpdated;
	}
	
	/**
	 * Delete player form database
	 * @param login
	 * 			must not be null
	 */
	public void deletePlayer(String login) {
		try(Connection connection = connector.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
			
			preparedStatement.setString(1, login);
			preparedStatement.executeUpdate();
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete all data from table player
	 */
	public void clean() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {
			statement.executeUpdate(DELETE_ALL);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private Player getPlayer(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			String login = resultSet.getString(COLUMN_LOGIN);
			String password = resultSet.getString(COLUMN_PASSWORD);
			int points = Integer.parseInt(resultSet.getString(COLUMN_POINTS));
			return new Player(login, password, points);
		}
		return null;
	}
	
}
