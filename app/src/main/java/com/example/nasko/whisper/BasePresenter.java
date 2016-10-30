package com.example.nasko.whisper;

public interface BasePresenter<V extends BaseView> {

    void init();

    void start();

    void stop();

    void destroy();
}
