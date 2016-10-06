package com.example.nasko.whisper.network.notifications.service;

import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import com.example.nasko.whisper.network.JsonDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;

public class SocketManager {

    private SimpleArrayMap<String, Observable<?>> observables;
    private Socket socket;
    private JsonDeserializer deserializer;

    public SocketManager(String endpoint) throws URISyntaxException {
        socket = IO.socket(endpoint);
        observables = new SimpleArrayMap<>();
        deserializer = new JsonDeserializer();
    }

    public <R> Observable<R> on(String event, Class<R> responseType) {
        return getObservable(event, responseType);
    }

    public <R> Observable<R> on(String event) {
        return getObservable(event, null);
    }

    public <R> Observable<R> getObservable(String event, Class<R> responseType) {
        Observable<?> eventObservable = observables.get(event);
        if (eventObservable == null) {
            eventObservable = createObservable(event, responseType);
            observables.put(event, eventObservable);
        }

        return (Observable<R>) eventObservable;
    }

    @NonNull
    private <R> Observable<?> createObservable(final String event, final Class<R> responseType) {
        return Observable.create(subscriber ->  {
            socket.on(event, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (subscriber.isUnsubscribed()) {
                        socket.off(event, this);
                        return;
                    }

                    if (responseType == null) {
                        // Nothing to deserialize
                        subscriber.onNext(null);
                        return;
                    }

                    if (args.length > 0) {
                        String json = args[0].toString();
                        try {
                            R result = deserializer.deserialize(json, responseType);
                            Log.d("OnNext", event + " " + socket.listeners(event).size());
                            subscriber.onNext(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }
            });
        });
    }

    public void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    public void emit(String event, Object[] args, Ack ack) {
        socket.emit(event, args, ack);
    }

    public boolean connected() {
        return socket.connected();
    }

    public void connect() {
        socket.connect();
    }

    public void dispose() {
        socket.disconnect();
        observables.clear();
    }
}
