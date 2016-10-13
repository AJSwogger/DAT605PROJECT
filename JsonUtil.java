package com.dat605;

import java.lang.reflect.Type;
import java.util.Map;
import spark.Request;
import spark.ResponseTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	public static Map<String, String> parseBody(Request request) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		return gson.fromJson(request.body(), type);
	}

	public static ResponseTransformer json() {
		return JsonUtil::toJson;
	}
}
