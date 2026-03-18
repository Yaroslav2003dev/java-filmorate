package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendRequestRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final EventService eventService;

    @Autowired
    public UserService(UserRepository userRepository, FriendRequestRepository friendRequestRepository, EventService eventService) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.eventService = eventService;
    }

    public Collection<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }


    public UserDto create(NewUserRequest request) {
        validateCreate(request);
        User user = UserMapper.mapToUser(request);
        Long id = userRepository.create(user);
        log.info("Создан пользователь с id =: {}", id);
        return UserMapper.mapToUserDto(userRepository.getUserById(id));
    }

    public UserDto update(UpdateUserRequest update) {
        User user = userRepository.getUserById(update.getId());
        validateUpdate(user, update);
        User updatedUser = UserMapper.updateUserFields(user, update);
        userRepository.update(updatedUser);
        log.info("Обновлена информация о пользователе c id = {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    private void validateCreate(NewUserRequest user) {
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
    }

    private void validateUpdate(User user, UpdateUserRequest newUser) {
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
    }


    public User getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    public UserDto addFriend(Long userId, Long friendId) {
        friendRequestRepository.addFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
        return UserMapper.mapToUserDto(userRepository.getUserById(friendId));
    }

    public UserDto deleteFriend(Long userId, Long friendId) {
        userRepository.getUserById(userId);
        userRepository.getUserById(friendId);
        friendRequestRepository.deleteFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
        return UserMapper.mapToUserDto(userRepository.getUserById(friendId));
    }

    public Collection<UserDto> getAllFriends(Long id) {
        userRepository.getUserById(id);
        return userRepository.findAllById(friendRequestRepository.findAllIdFriends(id)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        return userRepository.findAllById(friendRequestRepository.findIdCommonFriendsById(id, otherId)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

}
