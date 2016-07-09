package com.example.nasko.whisper.data.listeners;

import com.android.volley.VolleyError;
import com.example.nasko.whisper.User;

public abstract class OnAuthenticatedListener {

    public abstract void onAuthenticated(User user);

    public abstract void onError(VolleyError error);
}
