package com.example.nasko.whisper.data.socket;

import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import com.example.nasko.whisper.data.JsonDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.subjects.PublishSubject;

public class SocketManager {

    private static final String TAG = "SocketManager";

    private SimpleArrayMap<String, PublishSubject<?>> subjects;
    private Socket socket;
    private JsonDeserializer deserializer;

    public SocketManager(String endpoint, JsonDeserializer jsonDeserializer) throws URISyntaxException {
        try {
            socket = IO.socket(endpoint);
        } catch (URISyntaxException e) {
            Log.wtf(TAG, "Invalid socket endpoint :(");
            throw e;
        }

        deserializer = jsonDeserializer;
        subjects = new SimpleArrayMap<>();
    }

    <R> Observable<R> on(String event, Class<R> responseType) {
        return getObservable(event, responseType);
    }

    Observable<String> on(String event) {
        return getObservable(event, String.class);
    }

    <R> Observable<R> getObservable(String event, Class<R> responseType) {
        PublishSubject<?> eventSubject = subjects.get(event);
        if (eventSubject == null) {
            eventSubject = createSubject(event, responseType);
            subjects.put(event, eventSubject);
        }

        return (Observable<R>) eventSubject.asObservable();
    }

    @NonNull
    <R> PublishSubject<R> createSubject(final String event, final Class<R> responseType) {
        PublishSubject<R> eventSubject = PublishSubject.create();
        Emitter.Listener listener = args -> {
            if (! eventSubject.hasObservers()) {
                return;
            }

            // TODO: Research backpressure and fix MissingBackpressureException
            // when user is overwhelmed with notifications
            if (responseType == String.class) {
                // Nothing to deserialize
                Log.d("OnNext", event);
                eventSubject.onNext((R) event);
                return;
            }

            if (args.length > 0) {
                String json = args[0].toString();
                try {
                    R result = deserializer.deserialize(json, responseType);
                    Log.d("OnNext", event);
                    eventSubject.onNext(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        socket.on(event, listener);
        return eventSubject;
    }

    void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    boolean connected() {
        return socket.connected();
    }

    void connect() {
        socket.connect();
    }

    void dispose() {
        socket.disconnect();
        subjects.clear();
    }
}
