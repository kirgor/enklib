package com.kirgor.enklib.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JSONClient extends RESTClient {
    private static final Gson GSON = new Gson();

    public JSONClient(String baseUrl) {
        super(baseUrl, "application/json");
    }

    @Override
    protected <T> T parseEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException {
        return GSON.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), entityClass);
    }

    @Override
    protected <T> List<T> parseListEntity(Class<T> entityClass, HttpResponse httpResponse) throws IOException {
        JsonArray jsonArray = GSON.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), JsonArray.class);
        ArrayList<T> result = new ArrayList<T>();
        for (JsonElement jsonElement : jsonArray) {
            result.add(GSON.fromJson(jsonElement, entityClass));
        }
        return result;
    }

    @Override
    protected String payloadToString(Object payload) {
        return GSON.toJson(payload);
    }
}
