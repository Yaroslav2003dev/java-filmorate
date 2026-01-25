package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

    private final UserController userController = new UserController();
    private final FilmController filmController = new FilmController();


    @Test
    @DisplayName("Создание пользователя c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateUser() {
        //given
        User user = User.builder()
                .id(1L)
                .email("yaroslav@mail.com")
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
                .id(1L)
                .email(null)
                .login("Yar")
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        //when & then
        assertThrows(ValidationException.class, () -> userController.create(userNoEmail));
        assertTrue(userController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Создание пользователя при отсутствии символа @ в email")
    public void test_Create_WhenMissingSymbolInEmail_NotCreateUser() {
        //given
        User userNoSym = User.builder()
                .id(1L)
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
    @DisplayName("Создание пользователя при отсутствии имени")
    public void test_Create_WhenMissingName_CreateUser() {
        //given
        User userNoName = User.builder()
                .id(1L)
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
        User userNoSym = User.builder()
                .id(1L)
                .email("yaroslav@mail.com")
                .login(null)
                .name("Yaroslav")
                .birthday(LocalDate.of(2000, Month.OCTOBER, 8))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(userNoSym)
        );
        assertEquals("login не может быть пустым", exception.getMessage());
        assertTrue(userController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения из будущего")
    public void test_Create_WhenFutureBirthday_NotCreateUser() {
        //given
        User userFutureBirthday = User.builder()
                .id(1L)
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
    @DisplayName("Добавление фильма c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateFilm() {
        //given
        Film film = Film.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("Крутой фильм")
                .duration(114)
                .build();
        //when
        filmController.create(film);
        //then
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    @DisplayName("Добавление фильма с пустым названием")
    public void test_Create_WhenEmptyName_NotCreateFilm() {
        //given
        Film filmNoName = Film.builder()
                .id(1L)
                .name(null)
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("Крутой фильм")
                .duration(114)
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmNoName)
        );
        assertEquals("название не может быть пустым", exception.getMessage());
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Добавление фильма с большим описанием, в котором больше 200 символов")
    public void test_Create_WhenDescription201Characters_NotCreateFilm() {
        //given
        Film filmDescription201Characters = Film.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("a".repeat(201))
                .duration(114)
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmDescription201Characters)
        );
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    @DisplayName("Добавление фильма с датой раньше чем 28 декабря 1895 года")
    public void test_Create_WhenOldReleaseBefore28December1895_NotCreateFilm() {
        //given
        Film filmOldRelease = Film.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .description("Крутой фильм")
                .duration(114)
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmOldRelease)
        );
        assertEquals("дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
        assertTrue(filmController.findAll().isEmpty());
    }


    @Test
    @DisplayName("Добавление фильма c отрицательной продолжительностью")
    public void test_Create_WhenMinusDuration_NotCreateFilm() {
        //given
        Film filmMinusDuration = Film.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .description("Крутой фильм")
                .duration(-123)
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmMinusDuration)
        );
        assertEquals("продолжительность фильма должна быть положительным числом", exception.getMessage());
        assertTrue(filmController.findAll().isEmpty());
    }


}
