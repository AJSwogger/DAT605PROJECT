package com.dat605;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	//Database connection credentials
	private Connection connection;
	private final String url = "jdbc:mysql://localhost:3306/TodoApp";
	private final String username = "root";
	private final String password = "dat605";

	//Connect to Database
	public Connection connect() {

		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return connection;
	}
}
