package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for creating new tables in database
 * @author Filip
 *
 */
public class DBCreator {
	
	private static final String CREATE_TABLE_PLAYERS = 
			"CREATE TABLE `game`.`players` (\r\n" + 
			"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
			"  `login` VARCHAR(45) NOT NULL,\r\n" + 
			"  `password` VARCHAR(45) NOT NULL,\r\n" + 
			"  `points` INT NULL,\r\n" + 
			"  PRIMARY KEY (`id`, `login`),\r\n" + 
			"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
			"  UNIQUE INDEX `login_UNIQUE` (`login` ASC));\r\n";
	
	private static final String CREATE_TABLE_REPORTED_WORDS = 
			"CREATE TABLE reported_words (\r\n" + 
			"    id INT AUTO_INCREMENT PRIMARY KEY,\r\n" + 
			"    word VARCHAR(50) UNIQUE NOT NULL,\r\n" + 
			"    player_id INT,\r\n" + 
			"    FOREIGN KEY (player_id)\r\n" + 
			"        REFERENCES players (id)\r\n" + 
			"        ON DELETE CASCADE ON UPDATE CASCADE\r\n" + 
			");";
	
	private static final String CREATE_TABLE_GAMES = 
			"CREATE TABLE games (\r\n" + 
			"    id INT AUTO_INCREMENT PRIMARY KEY,\r\n" + 
			"    player_1 INT NOT NULL,\r\n" + 
			"    player_2 INT NOT NULL,\r\n" + 
			"    winner INT,\r\n" + 
			"    FOREIGN KEY (player_1)\r\n" + 
			"        REFERENCES players (id)\r\n" + 
			"        ON DELETE CASCADE ON UPDATE CASCADE,\r\n" + 
			"    FOREIGN KEY (player_2)\r\n" + 
			"        REFERENCES players (id)\r\n" + 
			"        ON DELETE CASCADE ON UPDATE CASCADE\r\n" + 
			")";
	
	private static final String DROP_TABLE_PLAYERS = "DROP TABLE IF EXISTS players";
	private static final String DROP_TABLE_REPORTED_WORDS = "DROP TABLE IF EXISTS reported_words";
	private static final String DROP_TABLE_GAMES = "DROP TABLE IF EXISTS games";
	
	private DBConnector connector;
	
	public DBCreator(DBConnector dbConnector) {
		connector = dbConnector;
	}
	
	/**
	 * This method creates a new table 'players' in the database
	 */
	public void createTablePlayers() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.execute(CREATE_TABLE_PLAYERS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a new table 'reported_words' in the database
	 */
	public void createTableReportedWords() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.execute(CREATE_TABLE_REPORTED_WORDS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a new table 'matches' in the database
	 */
	public void createTableGames() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.execute(CREATE_TABLE_GAMES);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dropTables() {
		try (Connection connection = connector.getConnection(); 
			 Statement statement = connection.createStatement()) {
			
			statement.execute(DROP_TABLE_REPORTED_WORDS);
			statement.execute(DROP_TABLE_GAMES);
			statement.execute(DROP_TABLE_PLAYERS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dropTableReportedWords() {
		try (Connection connection = connector.getConnection(); 
			 Statement statement = connection.createStatement()) {
			
			statement.execute(DROP_TABLE_REPORTED_WORDS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dropTableGames() {
		try (Connection connection = connector.getConnection(); 
			 Statement statement = connection.createStatement()) {
			
			statement.execute(DROP_TABLE_GAMES);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
