package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не был найден");
        }
        return users.get(id);
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return user;
    }

    @Override
    public Collection<User> getAllFriends(Long id) {
        User user = getUserById(id);
        ArrayList<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }


    @Override
    public User deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
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
}
