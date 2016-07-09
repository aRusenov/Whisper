package com.example.nasko.whisper.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nasko.whisper.User;

public class LocalUserRepository {

    private static final String PREFERENCES_FILE = "com.example.nasko.whisper";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ID = "uId";
    private static final String KEY_TOKEN = "sessionToken";

    private Context context;
    private SharedPreferences preferences;

    public LocalUserRepository(Context context) {
        this.context = context;
    }

    private SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        }

        return preferences;
    }

    public User getLoggedUser() {
        SharedPreferences preferences = this.getPreferences();
        String username = preferences.getString(KEY_USERNAME, null);
        String uId = preferences.getString(KEY_ID, null);
        String token = preferences.getString(KEY_TOKEN, null);

        return new User(username, uId, token);
    }

    public void saveLoginData(User user) {
        SharedPreferences preferences = this.getPreferences();

        preferences.edit().putString(KEY_USERNAME, user.getUsername()).apply();
        preferences.edit().putString(KEY_TOKEN, user.getSessionToken()).apply();
        preferences.edit().putString(KEY_ID, user.getUId()).apply();
    }

    public void logout() {
        SharedPreferences preferences = this.getPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(KEY_ID);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.apply();
    }
}
