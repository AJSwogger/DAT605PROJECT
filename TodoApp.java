package com.dat605;

import java.sql.*; // for standard JDBC programs
import java.util.ArrayList;
import java.util.Collection;
import org.json.simple.JSONObject;
import static spark.Spark.*;

public class TodoApp {

	private static Connection connection;
	private static String url = "jdbc:mysql://localhost:3306/TodoApp";
	private static String username = "root";
	private static String password = "dat605";

	public static void main(String[] args) {

		// Connect to Database
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connection Passed");
		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
		}

		post("/post", (ICRoute, response) -> {
			response.body("Test");
			return response;
			// Create something
			});

		get("/test",
				(request, response) -> {
					JSONObject obj = new JSONObject();
					try {

						PreparedStatement st = connection
								.prepareStatement("SELECT * FROM Todo");
						ResultSet r1 = st.executeQuery();
						ResultSetMetaData metaData = r1.getMetaData();
						int columnCount = metaData.getColumnCount();

						while (r1.next()) {
							int i = 1;
							while (i <= columnCount) {
								String value = r1.getString(i++);
								String key = r1.getString(i++);
								obj.put(value, key);
							}
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return obj;
				});
	}
}