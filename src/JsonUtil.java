package com.dat605;

import java.lang.reflect.Type;
import java.util.Map;
import spark.Request;
import spark.ResponseTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

	// Convert Object to JSON
	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	// Parse request body from JSON and return Map
	public static Map<String, String> parseBody(Request request) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		return gson.fromJson(request.body(), type);
	}

	// Create a ResponseTransformer that converts object to JSON
	public static ResponseTransformer json() {
		return JsonUtil::toJson;
	}
}
