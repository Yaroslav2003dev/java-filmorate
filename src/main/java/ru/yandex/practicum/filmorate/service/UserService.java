package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public Collection<User> findAll() {
        return userStorage.findAll();
    }


    public User create(User user) {
        if (user.getEmail() != null && user.getEmail().isBlank()) {
            log.warn("email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        } else if (user.getEmail() != null && !user.getEmail().contains("@")) {
            log.warn("email должен содержать @");
            throw new ValidationException("email должен содержать @");
        } else {
            user.setEmail(user.getEmail());
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

        return userStorage.create(user);
    }

    public User update(User newUser) {
        User user = userStorage.getUserById(newUser.getId());
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
        log.info("Обновлена информация о пользователе: {}", user);
        return user;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(Long id, Long friendId) {
        userStorage.addFriend(friendId, id);
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(friendId, id);
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getAllFriends(Long id) {
        return userStorage.getAllFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        Set<User> friendsUser = new HashSet<>(getAllFriends(id));
        Set<User> friendsOther = new HashSet<>(getAllFriends(otherId));
        friendsUser.retainAll(friendsOther);
        return friendsUser;
    }

}
