package com.example.spotifywrapped2340.SpotifyDataManagers;

import android.util.Log;

import com.google.firestore.v1.ArrayValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReader {
    JSONObject jsonObject;

    public enum ReadableType {
        STRING,
        BOOLEAN,
        INTEGER;
    }

    public static class ArrayValue {
        String key;
        ReadableType readableType;

        public ArrayValue(String key, ReadableType readableType) {
            this.key = key;
            this.readableType = readableType;
        }
    }


    public JsonReader(String jsonString) throws JSONException {
        this.jsonObject = new JSONObject(jsonString);
    }

    public String getStringValue(String key) throws JSONException {
        return jsonObject.getString(key);
    }

    public int getIntegerValue(String key) throws JSONException {
        return jsonObject.getInt(key);
    }

    public Map<String, Object> getJsonObjectData(String key, ArrayValue[] arrayValues) throws JSONException {
        JSONObject object = jsonObject.getJSONObject(key);

        Map<String, Object> returnedMap = new HashMap<>();

        for (ArrayValue value : arrayValues) {
            Log.d("Iteration", "reached");
            switch (value.readableType) {
                case STRING:
                    String objValue = object.getString(value.key);
                    returnedMap.put(value.key, objValue);
                    break;
                case BOOLEAN:
                    Boolean booleanValue = object.getBoolean(value.key);
                    returnedMap.put(value.key, booleanValue);
                    break;
                case INTEGER:
                    Log.d("Yes Brother", "Integer Reached");
                    Integer integerValue = object.getInt(value.key);
                    Log.d("Integer Value Null", integerValue == null ? "TRUE" : "FALSE");
                    Log.d("KEY", value.key);
                    returnedMap.put(value.key, integerValue);
                    break;
            }
        }

        return returnedMap;

    }

    public Map<String, ArrayList<Object>> getArrayValues(String key, ArrayValue[] arrayValues) throws JSONException {
        JSONArray array = jsonObject.getJSONArray(key);

        Map<String, ArrayList<Object>> returnedMap = new HashMap<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            for (ArrayValue value : arrayValues) {
                switch (value.readableType) {
                    case STRING:
                        ArrayList<Object> stringsArrayList = returnedMap.getOrDefault(value.key, new ArrayList<Object>());
                        String objValue = obj.getString(value.key);
                        stringsArrayList.add(objValue);
                        returnedMap.put(value.key, stringsArrayList);
                        break;
                    case BOOLEAN:
                        ArrayList<Object> booleanArrayList = returnedMap.getOrDefault(value.key, new ArrayList<Object>());
                        Boolean booleanValue = obj.getBoolean(value.key);
                        booleanArrayList.add(booleanValue);
                        returnedMap.put(value.key, booleanArrayList);
                        break;
                    case INTEGER:
                        ArrayList<Object> integerArrayList = returnedMap.getOrDefault(value.key, new ArrayList<Object>());
                        Integer integerValue = obj.getInt(value.key);
                        integerArrayList.add(integerValue);
                        returnedMap.put(value.key, integerArrayList);
                        break;
                }
            }
        }

        return returnedMap;
    }






}
