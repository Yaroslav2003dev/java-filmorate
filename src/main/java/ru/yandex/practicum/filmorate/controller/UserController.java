package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody NewUserRequest userRequest) {
        return new ResponseEntity<>(userService.create(userRequest), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody UpdateUserRequest updateUserRequest) {
        System.out.println(updateUserRequest);
        return new ResponseEntity<>(userService.update(updateUserRequest), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<UserDto> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return new ResponseEntity<>(userService.addFriend(id, friendId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<UserDto> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return new ResponseEntity<>(userService.deleteFriend(id, friendId), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<UserDto>> getAllFriends(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getAllFriends(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<UserDto>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return new ResponseEntity<>(userService.getCommonFriends(id, otherId), HttpStatus.OK);
    }

    @GetMapping("/{userid}/feed")
    public ResponseEntity<Collection<EventDto>> getEventsByUserId(@PathVariable Long userid) {
        return new ResponseEntity<>(eventService.getEventsByUserId(userid), HttpStatus.OK);
    }

    public User getUserById(Long id) {
        return userService.getUserById(id);
    }
}




