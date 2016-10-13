package com.dat605;

import java.sql.*;
import java.util.Base64;

import static com.dat605.JsonUtil.*;
import static spark.Spark.*;

public class TodoController {

	DatabaseConnection db;
	Connection connection;

	public TodoController(TodoItemService todoItemService, DatabaseConnection db) {

		this.db = db;

		// set port
		port(9090);

		// Authenticate User
		before((request, response) -> {
			try {
				if (connection == null || connection.isClosed()) {
					connection = db.connect();
				}
				Boolean authenticated = false;
				String auth = request.headers("Authorization");
				if (auth != null && auth.startsWith("Basic")) {
					String b64Credentials = auth.substring("Basic".length())
							.trim();
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
					response.type("application/json");
					halt((401));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		});

		after((request, response) -> {
			response.type("application/json");
		});

		// RESTful methods

		post("api/todos", "application/json",
				(request, response) -> todoItemService.createTodoItem(request,
						response), json());

		get("/api/todos",
				(request, response) -> todoItemService.getAllTodoItems(),
				json());

		get("/api/todos/:id",
				(request, response) -> todoItemService.getTodoItem(request,
						response), json());

		put("/api/todos/:id",
				(request, response) -> todoItemService.updateTodoItem(request,
						response), json());

		delete("/api/todos/:id",
				(request, response) -> todoItemService.removeTodoItem(request,
						response), json());
	}
}