package com.dat605;

import java.sql.*;
import java.util.Base64;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import static spark.Spark.*;

public class TodoApp {

	private static Connection connection;
	private static String url = "jdbc:mysql://localhost:3306/TodoApp";
	private static String username = "root";
	private static String password = "dat605";

	public static void main(String[] args) {

		port(9090);
		// Connect to Database
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connection Passed");
		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
		}
		
		//Authenticate User
		before((request, response) -> {
			Boolean authenticated = false;
			String auth = request.headers("Authorization");
			if (auth != null && auth.startsWith("Basic")) {
				String b64Credentials = auth.substring("Basic".length()).trim();
				String credentials = new String(Base64.getDecoder().decode(
						b64Credentials));
				System.out.println(credentials);
				if (credentials.equals("admin:admin"))
					authenticated = true;
			}
			if (!authenticated == true) {
				halt(401, "User Unauthorized");
			}
		});

		// RESTful methods

		post("/api/todos",
				(request, response) -> {
					String idTodo = request.queryParams("id");
					String todoDesc = request.queryParams("todoDesc");

					PreparedStatement st = connection
							.prepareStatement("INSERT INTO Todo (idTodo, todoDesc) VALUES (?, ?)");
					st.setString(1, idTodo);
					st.setString(2, todoDesc);

					st.executeUpdate();

					response.status(201); // 201 Created

					return response;

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
						return "No Records Found";
					} else {
						String idTodo = request.queryParams("id");
						String todoDesc = request.queryParams("todoDesc");

						PreparedStatement updateTodo = connection
								.prepareStatement("UPDATE Todo SET todoDesc=? WHERE idTodo=?");
						updateTodo.setString(1, todoDesc);
						updateTodo.setString(2, idTodo);

						updateTodo.executeUpdate();

						response.status(201); // 201 Created CHECK THIS!!!!!!!

						return response;
					}
				});

		delete("/api/todos/:id",
				(request, response) -> {
					String idTodo = request.params(":id");

					PreparedStatement deleteTodo = connection
							.prepareStatement("DELETE FROM Todo WHERE idTodo = ?");
					deleteTodo.setString(1, idTodo);
					deleteTodo.executeUpdate();

					return response;

				});
	}
}
