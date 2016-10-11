package com.example.nasko.whisper;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class ServiceBoundPresenter implements BasePresenter {

    private Subscription bindSub;
    private Subscription unbindSub;
    private CompositeSubscription serviceSubscriptions;

    protected SocketServiceBinder serviceBinder;
    protected UserProvider userProvider;
    protected SocketService service;

    protected ServiceBoundPresenter(SocketServiceBinder binder, UserProvider userProvider) {
        serviceBinder = binder;
        this.userProvider = userProvider;
        serviceSubscriptions = new CompositeSubscription();

        bindSub = serviceBinder.onBindService()
                .subscribe(service -> {
                    this.service = service;
                    onServiceBind(service, serviceSubscriptions);
                });

        unbindSub = serviceBinder.onUnbindService()
                .subscribe($ -> {
                    service = null;
                    serviceSubscriptions.clear();
                    onServiceUnbind();
                });
    }

    @Override
    public void start() {
        if (serviceBinder.isBound()) {
            service = serviceBinder.getService();
            onServiceBind(service, serviceSubscriptions);
        }

        serviceBinder.resume();
    }

    @Override
    public void stop() {
        service = null;
        serviceSubscriptions.clear();
        serviceBinder.pause();
    }

    @Override
    public void destroy() {
        bindSub.unsubscribe();
        unbindSub.unsubscribe();
    }

    public abstract void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions);

    public abstract void onServiceUnbind();
}
