package com.example.nasko.whisper.dagger.base;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = EmojiModule.class)
public interface EmojiComponent {

    List<String> emojis();
}
