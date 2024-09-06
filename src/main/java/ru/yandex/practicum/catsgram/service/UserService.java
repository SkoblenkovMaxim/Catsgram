package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> allUsers = new HashMap<>();

    public Collection<User> getAllUsers() {
        return allUsers.values();
    }

    public User createUser(@RequestBody User newUser) {
        if (newUser.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (!newUser.getEmail().equals(allUsers.get(newUser.getId()).getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        newUser.setRegistrationDate(Instant.now());
        newUser.setId(getNextId());
        allUsers.put(newUser.getId(), newUser);
        return newUser;
    }

    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (allUsers.containsKey(newUser.getId())) {
            User oldUser = allUsers.get(newUser.getId());

            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null) {
                oldUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }
            return newUser;
        }
        throw new DuplicatedDataException("Этот имейл уже используется");
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping("/users/{userId}")
    public Optional<User> findUserById(@PathVariable Long userId) {
        if (allUsers.containsKey(userId)) {
            return Optional.of(allUsers.get(userId));
        } else {
            return Optional.empty();
        }
    }
}
