package com.dat605;

public class Main {

	// Instantiate DatabaseConnection, TodoController and TodoItemService
	public static void main(String[] args) {
		DatabaseConnection db = new DatabaseConnection();
		new TodoController(new TodoItemService(db), db);
	}
}
