package model.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnector;

public class GamesRepository {

	private static final String INSERT = "INSERT INTO games(winner_id, looser_id) VALUES (?, ?);";
	private static final String GET_ALL_GAMES = "SELECT COUNT(*) FROM games WHERE winner_id = ? OR looser_id = ?;";
	private static final String GET_WON_GAMES = "SELECT COUNT(*) FROM games WHERE winner_id = ?;";
	
	private DBConnector connector;
	
	public GamesRepository(DBConnector dbConnector) {
		connector = dbConnector;
	}
	
	public boolean saveGame(long winnerID, long looserID) {
		boolean isInserted = false;
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
			
			preparedStatement.setLong(1, winnerID);
			preparedStatement.setLong(2, looserID);
			isInserted = preparedStatement.executeUpdate() > 0 ? true : false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isInserted;
	}
	
	public int getAllPlayerGames(long playerID) {
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
