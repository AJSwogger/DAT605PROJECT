package com.dat605;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.dat605.JsonUtil.*;
import spark.Request;
import spark.Response;

public class TodoItemService {

	private Connection connection;
	private DatabaseConnection db;

	// Constructor
	public TodoItemService(DatabaseConnection db) {
		this.db = db;
	}

	// returns a list of all todo items
	public List<TodoItem> getAllTodoItems(Request request, Response response) {
		ArrayList<TodoItem> todoList = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet r1 = null;
		try {
			// Connect to Database
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
			}

			st = connection.prepareStatement("SELECT * FROM Todo");
			r1 = st.executeQuery();
			ResultSetMetaData metaData = r1.getMetaData();
			int columnCount = metaData.getColumnCount();
			TodoItem todoItem = null;

			while (r1.next()) {
				int i = 1;

				while (i <= columnCount) {
					todoItem = new TodoItem(Integer.valueOf(r1.getString(i++)),
							r1.getString(i++), r1.getString(i++));
				}
				todoList.add(todoItem);
			}
		} catch (SQLException e) {
			response.status(500); // Internal Server Error
			e.printStackTrace();
		} finally {
			try {
				r1.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return todoList;
	}

	// returns a single todo item by id
	public TodoItem getTodoItem(Request request, Response response) {
		TodoItem todoItem = null;
		PreparedStatement st = null;
		ResultSet r1 = null;
		try {
			// Connect to Database
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
			}
			st = connection
					.prepareStatement("SELECT * FROM Todo WHERE idTodo = "
							+ request.params(":id"));
			r1 = st.executeQuery();
			ResultSetMetaData metaData = r1.getMetaData();
			int columnCount = metaData.getColumnCount();
			if (!r1.isBeforeFirst()) {
				response.status(404); // Not Found
			} else {
				while (r1.next()) {
					int i = 1;
					while (i <= columnCount) {
						todoItem = new TodoItem(Integer.valueOf(r1
								.getString(i++)), r1.getString(i++),
								r1.getString(i++));
					}
				}
			}
		} catch (SQLException e) {
			response.status(500); // Internal Server Error
			e.printStackTrace();
		} finally {
			try {
				r1.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return todoItem;
	}

	// creates a new todo item
	public TodoItem createTodoItem(Request request, Response response) {
		int success = 0;
		ResultSet r1 = null;
		ResultSetMetaData metaData = null;
		PreparedStatement st = null;
		Map<String, String> requestBody = parseBody(request);
		TodoItem todoItem = null;
		try {
			// Connect to Database
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
			}
			st = connection
					.prepareStatement("INSERT INTO Todo (todoDesc, complete) VALUES (?, ?)");
			st.setString(1, requestBody.get("todoDesc"));
			st.setString(2, requestBody.get("complete"));
			success = st.executeUpdate();
			if (success == 1) {
				response.status(201); // Created
				st = connection
						.prepareStatement("SELECT * FROM Todo ORDER BY idTodo DESC LIMIT 1");
				r1 = st.executeQuery();
				metaData = r1.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (r1.next()) {
					int i = 1;
					while (i <= columnCount) {
						todoItem = new TodoItem(Integer.valueOf(r1
								.getString(i++)), r1.getString(i++),
								r1.getString(i++));
					}
				}

			} else {
				response.status(400); // Bad Request
			}
		} catch (SQLException e) {
			response.status(500); // Internal Server Error
			e.printStackTrace();
		} finally {
			try {
				r1.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return todoItem;
	}

	// updates an existing todo item
	public TodoItem updateTodoItem(Request request, Response response) {

		Map<String, String> requestBody = parseBody(request);
		TodoItem todoItem = new TodoItem(
				Integer.valueOf(request.params(":id")),
				requestBody.get("todoDesc"), requestBody.get("complete"));
		PreparedStatement st = null;
		ResultSet r1 = null;
		try {
			// Connect to Database
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
			}
			st = connection
					.prepareStatement("SELECT * FROM Todo WHERE idTodo = "
							+ todoItem.getId());
			r1 = st.executeQuery();
			if (!r1.isBeforeFirst()) {
				response.status(404); // 404 Not found
			} else {
				PreparedStatement updateTodo = connection
						.prepareStatement("UPDATE Todo SET todoDesc=?, complete=? WHERE idTodo=?");
				updateTodo.setString(1, todoItem.getTodoDesc());
				updateTodo.setString(2, todoItem.getComplete());
				updateTodo.setString(3, request.params(":id"));
				int success = updateTodo.executeUpdate();
				if (success == 1) {
					response.status(201); // Created
				} else {
					response.status(400); // Bad Request
				}
			}
		} catch (SQLException e) {
			response.status(500); // Internal Server Error
			e.printStackTrace();
		} finally {
			try {
				r1.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return todoItem;
	}

	public int removeTodoItem(Request request, Response response) {
		String idTodo = request.params(":id");
		int success = 0;
		PreparedStatement st = null;
		try {
			// Connect to Database
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
			}
			st = connection
					.prepareStatement("DELETE FROM Todo WHERE idTodo = ?");
			st.setString(1, idTodo);
			success = st.executeUpdate();
			if (success == 1) {
				response.status(204);
			} else {
				response.status(404); // 404 Not found
			}
		} catch (SQLException e) {
			response.status(500); // Internal Server Error
			e.printStackTrace();
		} finally {
			try {
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
}
