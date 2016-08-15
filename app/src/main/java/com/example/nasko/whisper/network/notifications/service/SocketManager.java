package com.example.nasko.whisper.network.notifications.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.nasko.whisper.network.JsonDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;

public class SocketManager {

    private HashMap<String, Observable<?>> observables;
    private Socket socket;
    private JsonDeserializer deserializer;

    public SocketManager(String endpoint) throws URISyntaxException {
        socket = IO.socket(endpoint);
        observables = new HashMap<>();
        deserializer = new JsonDeserializer();
    }

    public <R> Observable<R> on(String event, Class<R> responseType) {
        Observable<?> eventObservable = observables.get(event);
        if (eventObservable == null) {
            eventObservable = createObservable(event, responseType);
            observables.put(event, eventObservable);
        }

        return (Observable<R>) eventObservable;
    }

    @NonNull
    private <R> Observable<?> createObservable(final String event, final Class<R> responseType) {
        Observable<?> eventObservable = Observable.create(subscriber ->  {
            socket.on(event, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (subscriber.isUnsubscribed()) {
                        socket.off(event, this);
                        return;
                    }

                    R result = null;
                    if (args.length > 0) {
                        String json = args[0].toString();
                        try {
                            result = deserializer.deserialize(json, responseType);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("OnNext", event + " " + socket.listeners(event).size());
                    subscriber.onNext(result);
                }
            });
        });
        return eventObservable;
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
