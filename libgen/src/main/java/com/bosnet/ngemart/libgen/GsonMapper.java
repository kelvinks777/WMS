package com.bosnet.ngemart.libgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Luis Ginanjar on 29/04/2016.
 * Purpose :
 */
public class GsonMapper {

    public static final String DEFAULT_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public GsonMapper() {
        gson = new GsonBuilder()
                .setDateFormat(DEFAULT_API_DATE_FORMAT)
                .create();
    }

    public GsonMapper(String dateFormat){
        gson = new GsonBuilder()
                .setDateFormat(dateFormat)
                .create();
    }

    Gson gson;

    public <T> T read(String jsonString, Class<T> theClass) {
        return gson.fromJson(jsonString, theClass);
    }

    public <T> List<T> readList(String jsonString, Class<T[]> theClass) {
        return Arrays.asList(gson.fromJson(jsonString, theClass));
    }

    public <T> String write(T data) {
        return gson.toJson(data);
    }

    public <T> String writeList(List<T> data) {
        return gson.toJson(data);
    }

}
