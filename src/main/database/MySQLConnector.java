package database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This class is responsible for providing a connection to the MySQL database.
 * 
 * @author Filip K.
 */
public class MySQLConnector implements DBConnector {

	private final static String DB_URL = "jdbc:mysql://localhost:3306/game?useSSL=false";
	private final static String DB_USER = "root";
	private final static String DB_PASS = "****";
	private final static String DB_DRIVER = "com.mysql.jdbc.Driver";

	private static MySQLConnector mySQLConnector;
	
	private ComboPooledDataSource dataSource;

	private MySQLConnector() {
		setupDataSource();
	}
	
	public static MySQLConnector getInstance() {
		if(mySQLConnector == null) {
			mySQLConnector = new MySQLConnector();
			return mySQLConnector;
		} else {
			return mySQLConnector;
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void closeConnection() throws SQLException {
		dataSource.close();
	}
	
	private void setupDataSource() {
		dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(DB_DRIVER);
			dataSource.setJdbcUrl(DB_URL);
			dataSource.setUser(DB_USER);
			dataSource.setPassword(DB_PASS);
			
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
}
