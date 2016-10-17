package com.dat605;

import java.sql.*;
import java.util.Base64;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import static spark.Spark.*;

public class TodoApp {

	// database connection information
	private static Connection connection;
	private static String url = "jdbc:mysql://localhost:3306/TodoApp";
	private static String username = "root";
	private static String password = "dat605";

	public static void main(String[] args) {

		// set port
		port(9090);

		// Connect to Database
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connection Passed");
		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
		}

		// Authenticate User
		before((request, response) -> {
			Boolean authenticated = false;
			String auth = request.headers("Authorization");
			if (auth != null && auth.startsWith("Basic")) {
				String b64Credentials = auth.substring("Basic".length()).trim();
				String credentials = new String(Base64.getDecoder().decode(
						b64Credentials));

				String[] fields = credentials.split(":");
				
				PreparedStatement st = connection
						.prepareStatement("SELECT * FROM User WHERE idUser = \'"
								+ fields[0] + "\'");
				ResultSet r1 = st.executeQuery();
				String user = null;
				if (r1.next()) {
					int i = 1;
					user = r1.getString(i++) + ":" + r1.getString(i++);
				}
				System.out.println(user);
				if (credentials.equals(user))
					authenticated = true;
			}
			if (!authenticated == true) {
				halt(401, "User Unauthorized");
			}
		});

		// RESTful methods

		post("/api/todos",
				(request, response) -> {
					String todoDesc = request.queryParams("todoDesc");
					String complete = request.queryParams("complete");

					PreparedStatement st = connection
							.prepareStatement("INSERT INTO Todo (todoDesc, complete) VALUES (?, ?)");
					st.setString(1, todoDesc);
					st.setString(2, complete);
					int success = st.executeUpdate();
					if (success == 1) {
						response.status(201); // 201 Created
						return "Successfully Created Todo Item \"" + todoDesc
								+ "\"";
					} else {
						response.status(400);
						return "Error: Could Not Create a New Todo Item ";
					}
				});

		get("/api/todos/:id",
				(request, response) -> {

					JSONObject obj = new JSONObject();
					try {

						PreparedStatement st = connection
								.prepareStatement("SELECT * FROM Todo WHERE idTodo = "
										+ request.params(":id"));
						ResultSet r1 = st.executeQuery();
						ResultSetMetaData metaData = r1.getMetaData();
						int columnCount = metaData.getColumnCount();

						if (!r1.isBeforeFirst()) {
							response.status(404); // 404 Not found
							return "No Records Found";
						} else {

							while (r1.next()) {
								int i = 1;
								while (i <= columnCount) {
									obj.put(metaData.getColumnLabel(i),
											r1.getString(i++));
								}
							}
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					return obj;
				});

		get("/api/todos",
				(request, response) -> {

					JSONArray jsonArray = new JSONArray();

					try {
						PreparedStatement st = connection
								.prepareStatement("SELECT * FROM Todo");
						ResultSet r1 = st.executeQuery();
						ResultSetMetaData metaData = r1.getMetaData();
						int columnCount = metaData.getColumnCount();

						while (r1.next()) {
							int i = 1;
							JSONObject obj = new JSONObject();
							while (i <= columnCount) {
								obj.put(metaData.getColumnLabel(i),
										r1.getString(i++));
							}
							jsonArray.put(obj);
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					JSONObject obj1 = new JSONObject();
					obj1.put("todos", jsonArray);
					return obj1;
				});

		put("/api/todos/:id",
				(request, response) -> {
					// String id = request.params(":id");
					PreparedStatement st = connection
							.prepareStatement("SELECT * FROM Todo WHERE idTodo = "
									+ request.params(":id"));
					ResultSet r1 = st.executeQuery();
					if (!r1.isBeforeFirst()) {
						response.status(404); // 404 Not found
						return "Todo Item " + request.params(":id")
								+ " Does Not Exist and Could Not Be Updated";
					} else {
						String todoDesc = request.queryParams("todoDesc");
						String complete = request.queryParams("complete");

						PreparedStatement updateTodo = connection
								.prepareStatement("UPDATE Todo SET todoDesc=?, complete=? WHERE idTodo=?");
						updateTodo.setString(1, todoDesc);
						updateTodo.setString(2, complete);
						updateTodo.setString(3, request.params(":id"));
						int success = updateTodo.executeUpdate();

						if (success == 1) {
							response.status(201);
							return "Successfully Updated Todo Item "
									+ request.params(":id");
						} else {
							response.status(404);
							return "Error: Could Not Update Todo Item "
									+ request.params(":id");
						}
					}
					
				});

		delete("/api/todos/:id",
				(request, response) -> {
					String idTodo = request.params(":id");

					PreparedStatement st = connection
							.prepareStatement("DELETE FROM Todo WHERE idTodo = ?");
					st.setString(1, idTodo);
					int success = st.executeUpdate();
					if (success == 1) {
						response.status(200);
						return "Successfully Deleted Todo Item "
								+ request.params(":id");
					} else {
						response.status(404); // 404 Not found
						return "Todo Item " + request.params(":id")
								+ " Does Not Exist and Could Not Be Deleted";
					}
				});
		
		
	}
	
	public void closeConnections() {
		
	}
}
