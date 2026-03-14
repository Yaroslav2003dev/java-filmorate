package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTests {
    @Autowired
    private UserController userController;

    @Test
    @DisplayName("Создание пользователя c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateUser() {
        //given
        NewUserRequest user = NewUserRequest.builder()
                .email("1@")
                .login("Yar1")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        //when
        UserDto userDto = userController.create(user).getBody();
        //then
        Assertions.assertNotNull(userDto);
        assertEquals(userDto.getLogin(), userController.getUserById(userDto.getId()).getLogin());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии email")
    void test_Create_WhenEmailIsNull_NotCreateUser() {
        //given
        NewUserRequest userNoEmail = NewUserRequest.builder()
                .email(null)
                .login("Yar2")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        //when
        UserDto userDto = userController.create(userNoEmail).getBody();
        //then
        Assertions.assertNotNull(userDto);
        assertEquals(userDto.getLogin(), userController.getUserById(userDto.getId()).getLogin());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии символа @ в email")
    public void test_Create_WhenMissingSymbolInEmail_NotCreateUser() {
        //given
        NewUserRequest userNoSym = NewUserRequest.builder()
                .email("yaroslavmail.com")
                .login("Yar3")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(userNoSym)
        );
        assertEquals("email должен содержать @", exception.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя, у которого отсутствует имя")
    public void test_Create_WhenMissingName_CreateUser() {
        //given
        NewUserRequest userNoName = NewUserRequest.builder()
                .email("yaroslav@mail.com")
                .login("Yar4")
                .name(null)
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when
        UserDto userDto = userController.create(userNoName).getBody();
        //then
        Assertions.assertNotNull(userDto);
        assertEquals(userDto.getLogin(), userController.getUserById(userDto.getId()).getLogin());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии логина")
    public void test_Create_WhenMissingLogin_NotCreateUser() {
        //given
        NewUserRequest userNoLogin = NewUserRequest.builder()
                .email("yaroslav1@mail.com")
                .login(null)
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when
        UserDto userDto = userController.create(userNoLogin).getBody();
        // then
        Assertions.assertNotNull(userDto);
        assertEquals(userDto.getEmail(), userController.getUserById(userDto.getId()).getEmail());
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения из будущего")
    public void test_Create_WhenFutureBirthday_NotCreateUser() {
        //given
        NewUserRequest userFutureBirthday = NewUserRequest.builder()
                .email("yaroslav2@mail.com")
                .login("Yar5")
                .name("Yaroslav")
                .birthday(LocalDate.of(2300, Month.OCTOBER, 8))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(userFutureBirthday)
        );
        assertEquals("дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление email на null")
    public void test_Update_WhenMissingLogin_NoUpdateUser() {
        //given
        NewUserRequest user = NewUserRequest.builder()
                .email("yaroslav3@mail.com")
                .login("Yar6")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        UpdateUserRequest userNoEmail = UpdateUserRequest.builder()
                .id(1L)
                .email(null)
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when
        UserDto userDto = userController.create(user).getBody();
        userController.update(userNoEmail);
        // then
        Assertions.assertNotNull(userDto);
        assertNull(userController.getUserById(userDto.getId()).getEmail());
    }
}
