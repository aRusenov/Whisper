package com.example.nasko.whisper.network.listeners;

import com.android.volley.VolleyError;
import com.example.nasko.whisper.models.User;

public abstract class OnAuthenticatedListener {

    public abstract void onAuthenticated(User user);

    public abstract void onError(VolleyError error);
}
