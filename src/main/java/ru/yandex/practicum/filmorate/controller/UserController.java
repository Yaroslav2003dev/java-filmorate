package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getEmail() != null && user.getEmail().isBlank()) {
            log.warn("email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        } else if (user.getEmail() != null && !user.getEmail().contains("@")) {
            log.warn("email должен содержать @");
            throw new ValidationException("email должен содержать @");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (user.getLogin() != null && user.getLogin().isBlank()) {
            log.warn("login не может быть пустым");
            throw new ValidationException("login не может быть пустым");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }


    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        User user = users.get(newUser.getId());
        if (newUser.getEmail() != null && newUser.getEmail().isBlank()) {
            log.warn("email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        } else if (newUser.getEmail() != null && !newUser.getEmail().contains("@")) {
            log.warn("email должен содержать @");
            throw new ValidationException("email должен содержать @");
        } else {
            user.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null && newUser.getLogin().isBlank()) {
            log.warn("login не может быть пустым");
            throw new ValidationException("login не может быть пустым");
        } else {
            user.setLogin(newUser.getLogin());
        }

        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        } else {
            user.setBirthday(newUser.getBirthday());
        }

        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }


        users.put(user.getId(), user);
        log.info("Обновлена информация о пользователе: {}", user);
        return user;
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}




