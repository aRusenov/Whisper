package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.network.misc.JsonDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static final String TAG = SocketManager.class.getName();

    private HashMap<String, List<SocketEventListener<?>>> eventListeners;
    private Socket socket;
    private JsonDeserializer deserializer;

    public SocketManager(String endpoint) throws URISyntaxException {
        socket = IO.socket(endpoint);

        eventListeners = new HashMap<>();
        deserializer = new JsonDeserializer();
    }

    public <R> SocketManager on(String event, Class<R> responseType, SocketEventListener<R> callback) {
        List<SocketEventListener<?>> listeners = eventListeners.get(event);
        if (listeners == null) {
            listeners = new ArrayList<>();
            eventListeners.put(event, listeners);

            socket.on(event, args -> {
                R result = null;
                if (args.length > 0) {
                    String json = args[0].toString();
                    try {
                        result = deserializer.deserialize(json, responseType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for (SocketEventListener<?> listener : eventListeners.get(event)) {
                    ((SocketEventListener<R>)listener).onResult(result);
                }
            });
        }

        listeners.add(callback);
        return this;
    }

    public void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    public boolean connected() {
        return socket.connected();
    }

    public void connect() {
        socket.connect();
    }

    public void disconnect() {
        socket.disconnect();
    }
}
