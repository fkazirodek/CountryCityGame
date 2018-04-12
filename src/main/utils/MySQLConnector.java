package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is responsible for providing a connection to the MySQL database
 * 
 * @author Filip K.
 */
public class MySQLConnector implements DBConnector {

	private final static String DB_URL = "jdbc:mysql://localhost:3306/game?useSSL=false";
	private final static String DB_USER = "root";
	private final static String DB_PASS = "******";
	private final static String DB_DRIVER = "com.mysql.jdbc.Driver";

	private static Connection connection;

	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(DB_DRIVER);
		connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		return connection;
	}

	@Override
	public void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();
	}
}
