package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.dagger.base.ApplicationModule;
import com.example.nasko.whisper.dagger.base.BaseComponent;
import com.example.nasko.whisper.dagger.base.DaggerBaseComponent;
import com.example.nasko.whisper.dagger.base.DaggerEmojiComponent;
import com.example.nasko.whisper.dagger.base.EmojiComponent;
import com.example.nasko.whisper.dagger.base.EmojiModule;
import com.example.nasko.whisper.dagger.rest.RestComponent;
import com.example.nasko.whisper.dagger.rest.RestModule;
import com.example.nasko.whisper.dagger.user.SocketModule;
import com.example.nasko.whisper.dagger.user.UserComponent;
import com.example.nasko.whisper.utils.helpers.ConfigLoader;
import com.squareup.leakcanary.LeakCanary;

public class WhisperApplication extends Application {

    private static String apiUrl;
    private static BaseComponent baseComponent;
    private static EmojiComponent emojiComponent;
    private static RestComponent restComponent;
    private static UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);

        baseComponent = DaggerBaseComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        emojiComponent = DaggerEmojiComponent.builder()
                .emojiModule(new EmojiModule())
                .build();

        apiUrl = ConfigLoader.getConfigValue(getApplicationContext(), "api_url");
        restComponent = baseComponent.plus(new RestModule(apiUrl));
    }

    public static BaseComponent baseComponent() {
        return baseComponent;
    }

    public static EmojiComponent getEmojiComponent() {
        return emojiComponent;
    }

    public static RestComponent restComponent() {
        return restComponent;
    }

    public static UserComponent userComponent() {
        if (userComponent == null) {
            userComponent = baseComponent.plus(new SocketModule(apiUrl));
        }

        return userComponent;
    }

    public static void releaseUserComponent() {
        userComponent = null;
    }
}
