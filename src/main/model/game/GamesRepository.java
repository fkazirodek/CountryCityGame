package model.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import database.DBConnector;

/**
 * The class responsible for accessing the database, performing operations on DB 
 * and returning the results of these operations
 * @author Filip
 *
 */
public class GamesRepository {

	private static final String INSERT = "INSERT INTO games(player_1, player_2, winner) VALUES (?, ?, ?);";
	private static final String GET_ALL_GAMES = "SELECT COUNT(*) FROM games WHERE player_1 = ? OR player_2 = ?;";
	private static final String GET_WON_GAMES = "SELECT COUNT(*) FROM games WHERE winner = ?;";
	
	private DBConnector connector;
	
	public GamesRepository(DBConnector dbConnector) {
		connector = dbConnector;
	}
	
	/**
	 * Save the new game with the participants and the winner
	 * @param player1
	 * @param player2
	 * @param winner
	 * @return true if games was saved, false otherwise
	 */
	public boolean saveGame(long player1, long player2, Long winner) {
		boolean isInserted = false;
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
			
			preparedStatement.setLong(1, player1);
			preparedStatement.setLong(2, player2);
			if(winner == null)
				preparedStatement.setNull(3, Types.INTEGER);
			else
				preparedStatement.setLong(3, winner);
			isInserted = preparedStatement.executeUpdate() > 0 ? true : false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isInserted;
	}
	
	/**
	 * Get games played by player which ID was passed as parameter
	 * @param playerID
	 * @return number of played games
	 */
	public int getAllPlayedGames(long playerID) {
		int numberOfGames = 0;
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_GAMES)) {
			
			preparedStatement.setLong(1, playerID);
			preparedStatement.setLong(2, playerID);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
				numberOfGames = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfGames;
	}
	
	/**
	 * Get only won games played by player which ID was passed as parameter
	 * @param playerID
	 * @return number of won games
	 */
	public int getWonPlayerGames(long playerID) {
		int numberOfGames = 0;
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(GET_WON_GAMES)) {
			
			preparedStatement.setLong(1, playerID);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
				numberOfGames = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfGames;
	}
}
