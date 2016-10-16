package com.dat605;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private Connection connection;
	private final String url = "jdbc:mysql://localhost:3306/TodoApp";
	private final String username = "root";
	private final String password = "dat605";

	public Connection connect() {

		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connection Passed");
		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
		} 
		return connection;
	}
}
