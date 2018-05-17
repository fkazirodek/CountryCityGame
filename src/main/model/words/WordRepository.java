package model.words;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.DBConnector;

public class WordRepository {

	private static final String INSERT = "INSERT INTO reported_words(word, player_id) VALUES(?, ?)";
	
	DBConnector connector;

	public WordRepository(DBConnector connector) {
		this.connector = connector;
	}
	
	public boolean saveReportedWord(String word, long player_id) {
		boolean isInserted = false;
		try(Connection connection = connector.getConnection(); 
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
			
			preparedStatement.setString(1, word);
			preparedStatement.setLong(2, player_id);
			
			isInserted = preparedStatement.executeUpdate() > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isInserted;
	}
	
}
