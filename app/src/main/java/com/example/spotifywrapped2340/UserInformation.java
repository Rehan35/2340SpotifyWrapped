package com.example.spotifywrapped2340;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;

public class UserInformation {
    String userId;

    public static String name;
    public static HashMap<String, String[]> trackLists;
    public static String getName() {
        return name;
    }
    public static void setName(String name) {
        UserInformation.name = name;
    }

    public static void parseInfo(JSONObject object) {
        try {
            setName(object.getString("display_name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseTracks(String term, JSONObject object) {
        String[] array = new String[5];
        try {
            JSONArray items = object.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject trackObject = items.getJSONObject(i);
                String trackName = trackObject.getJSONObject("name").getString(term);
                array[i] = trackName;
            }

            trackLists.put(term, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
