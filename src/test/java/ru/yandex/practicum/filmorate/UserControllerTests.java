package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTests {
    private final UserController userController = new UserController();

    @Test
    @DisplayName("Создание пользователя c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateUser() {
        //given
        User user = User.builder()
                .email("@")
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        //when
        userController.create(user);
        //then
        assertEquals(1, userController.findAll().size());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии email")
    void test_Create_WhenEmailIsNull_NotCreateUser() {
        //given
        User userNoEmail = User.builder()
                .email(null)
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        //when
        userController.create(userNoEmail);
        //then
        assertEquals(1, userController.findAll().size());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии символа @ в email")
    public void test_Create_WhenMissingSymbolInEmail_NotCreateUser() {
        //given
        User userNoSym = User.builder()
                .email("yaroslavmail.com")
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(userNoSym)
        );
        assertEquals("email должен содержать @", exception.getMessage());
        assertTrue(userController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Создание пользователя, у которого отсутствует имя")
    public void test_Create_WhenMissingName_CreateUser() {
        //given
        User userNoName = User.builder()
                .email("yaroslav@mail.com")
                .login("Yar")
                .name(null)
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when
        userController.create(userNoName);
        //then
        assertEquals(1, userController.findAll().size());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии логина")
    public void test_Create_WhenMissingLogin_NotCreateUser() {
        //given
        User userNoLogin = User.builder()
                .email("yaroslav@mail.com")
                .login(null)
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when & then
        userController.create(userNoLogin);
        assertEquals(1, userController.findAll().size());
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения из будущего")
    public void test_Create_WhenFutureBirthday_NotCreateUser() {
        //given
        User userFutureBirthday = User.builder()
                .email("yaroslav@mail.com")
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2300, Month.OCTOBER, 8))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(userFutureBirthday)
        );
        assertEquals("дата рождения не может быть в будущем", exception.getMessage());
        assertTrue(userController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Обновление email на null")
    public void test_Update_WhenMissingLogin_NoUpdateUser() {
        //given
        User user = User.builder()
                .id(1L)
                .email("yaroslav@mail.com")
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        User userNoEmail = user.toBuilder()
                .email(null)
                .build();
        // when
        userController.create(user);
        userController.update(userNoEmail);
        // then
        assertEquals(null, userController.getUserById(1L).getEmail());
    }
}
