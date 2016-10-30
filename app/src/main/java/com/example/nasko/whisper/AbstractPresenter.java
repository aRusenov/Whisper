package com.example.nasko.whisper;

import rx.subscriptions.CompositeSubscription;

public abstract class AbstractPresenter<V extends BaseView> implements BasePresenter<V> {

    protected CompositeSubscription subscriptions;
    protected V view;

    protected AbstractPresenter(V view) {
        this.view = view;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() { }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        subscriptions.clear();
        view = null;
    }
}
