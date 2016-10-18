package com.example.nasko.whisper.dagger.base;

import android.content.Context;

import com.example.nasko.whisper.data.notifications.MessageNotificationController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    @Provides @Singleton MessageNotificationController provideNotificationController(Context context) {
        return new MessageNotificationController(context);
    }
}
