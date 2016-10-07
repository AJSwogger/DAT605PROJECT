package com.dat605;

import static spark.Spark.*;

public class TodoApp {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}