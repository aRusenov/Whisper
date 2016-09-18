package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.os.Bundle;

import com.example.nasko.whisper.network.notifications.consumer.OnServiceBoundListener;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.views.contracts.View;

public abstract class ServiceBoundPresenter<V extends View> extends AbstractPresenter<V> implements OnServiceBoundListener {

    protected SocketServiceBinder serviceBinder;
    protected SocketService service;

    protected ServiceBoundPresenter(SocketServiceBinder binder) {
        serviceBinder = binder;
    }

    @Override
    public void attachView(V view, Context context, Bundle extras) {
        super.attachView(view, context, extras);
        if (serviceBinder.isBound()) {
            service = serviceBinder.getService();
            onServiceBind(service);
        }

        serviceBinder.addServiceBoundListener(this);
    }

    @Override
    public void onServiceBind(SocketService service) {
        this.service = service;
    }

    @Override
    public void onServiceUnbind() {
        service = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceBinder.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceBinder.pause();
    }

    @Override
    public void detachView() {
        super.detachView();
        service = null;
        serviceBinder.removeServiceBoundListener(this);
    }
}
