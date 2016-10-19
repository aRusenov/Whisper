package com.example.nasko.whisper.dagger.base;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EmojiModule {

    private static final int INITIAL_CHARACTER = 0x1F601;
    private static final int COUNT = 30;

    @Provides @Singleton List<String> provideEmojis() {
        List<String> emojis = new ArrayList<>(COUNT);
        int ch = INITIAL_CHARACTER;
        int[] codePoints = new int[1];
        for (int i = 0; i < COUNT; i++) {
            codePoints[0] = ch++;
            emojis.add(new String(codePoints, 0, 1));
        }

        return emojis;
    }
}
