package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.presenters.register.RegisterPresenter;

public interface RegisterView extends View<RegisterPresenter> {

    void displayError(String message);
}
