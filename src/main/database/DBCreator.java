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
			"  `password` VARCHAR(45) NULL,\r\n" + 
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
	
	private static final String CREATE_TABLE_MATCHES = 
			"CREATE TABLE games (\r\n" + 
			"    id INT AUTO_INCREMENT PRIMARY KEY,\r\n" + 
			"    winner_id INT,\r\n" + 
			"    looser_id INT,\r\n" + 
			"    FOREIGN KEY (winner_id)\r\n" + 
			"        REFERENCES players (id),\r\n" + 
			"    FOREIGN KEY (looser_id)\r\n" + 
			"        REFERENCES players (id)\r\n" + 
			");";
	
	private DBConnector connector;
	
	public DBCreator(DBConnector dbConnector) {
		connector = dbConnector;
	}
	
	/**
	 * This method creates a new table 'players' in the database
	 */
	public void createTablePlayer() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.executeQuery(CREATE_TABLE_PLAYERS);
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
			statement.executeQuery(CREATE_TABLE_REPORTED_WORDS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a new table 'matches' in the database
	 */
	public void createTableMatches() {
		try(Connection connection = connector.getConnection();
			Statement statement = connection.createStatement()) {		
			statement.executeQuery(CREATE_TABLE_MATCHES);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
