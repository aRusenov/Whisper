package com.example.nasko.whisper.managers;

import com.example.nasko.whisper.models.User;

public class AppUserProvider implements UserProvider {

    private LocalUserRepository userRepository;
    private User user;

    public AppUserProvider(LocalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        if (user == null) {
            user = userRepository.getLoggedUser();
        }

        return user;
    }

    @Override
    public void setCurrentUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        userRepository.saveLoginData(user);
        this.user = user;
    }

    @Override
    public void logout() {
        userRepository.logout();
    }
}
