package com.fyp.cls.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public <T> void putObject(String key, T object) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public <T> T getObject(String key, Class<T> type) {
        String json = sharedPreferences.getString(key, null);
        if (json != null) {
            return gson.fromJson(json, type);
        }
        return null;
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
