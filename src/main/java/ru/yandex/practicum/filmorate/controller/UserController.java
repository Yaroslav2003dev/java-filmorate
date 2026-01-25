package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            validate(user);
        } catch (ValidationException e) {
            log.warn("Ошибка при добавлении пользователя", e);
            throw e;
        }

        if ((user.getName() == null || user.getName().isBlank())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }


    @PutMapping
    public User update(@RequestBody User newUser) {
        try {
            validate(newUser);
        } catch (ValidationException e) {
            log.warn("Ошибка при обновлении информации о пользователе", e);
            throw e;
        }

        User user = users.get(newUser.getId());
        user.setBirthday(newUser.getBirthday());
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        user.setLogin(newUser.getLogin());
        users.put(user.getId(), user);
        log.info("Обновлена информация о пользователе: {}", user);
        return user;
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("email не может быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("email должен содержать @");
        }
        if ((user.getLogin() == null || user.getLogin().isBlank())) {
            throw new ValidationException("login не может быть пустым");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }
}




