package com.dat605;

public class TodoItem {
	private int idTodo;
	private String todoDesc;
	private String complete;

	// TodoIem Constructor
	public TodoItem(int idTodo, String todoDesc, String complete) {
		this.idTodo = idTodo;
		this.todoDesc = todoDesc;
		this.complete = complete;
	}

	// TodoItem Getters and Setters
	public int getId() {
		return idTodo;
	}

	public void setId(int id) {
		this.idTodo = id;
	}

	public String getTodoDesc() {
		return todoDesc;
	}

	public void setTodoDesc(String todoDesc) {
		this.todoDesc = todoDesc;
	}

	public String getComplete() {
		return complete;
	}

	public void setComplete(String complete) {
		this.complete = complete;
	}
}
