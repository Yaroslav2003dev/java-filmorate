package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTests {
    private final FilmController filmController = new FilmController();

    @Test
    @DisplayName("Добавление фильма c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateFilm() {
        //given
        Film film = Film.builder()
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
    @DisplayName("Добавление фильма с нулевым описанием")
    public void test_Create_filmWithNullDescription_CreateFilm() {
        //given
        Film filmWithNullDescription = Film.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description(null)
                .duration(114)
                .build();
        // when
        filmController.create(filmWithNullDescription);
        //then
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    @DisplayName("Добавление фильма с датой раньше чем 28 декабря 1895 года")
    public void test_Create_WhenOldReleaseBefore28December1895_NotCreateFilm() {
        //given
        Film filmOldRelease = Film.builder()
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

    @Test
    @DisplayName("Обновление продолжительности фильма на null")
    public void test_Update_WhenNullDuration_NoUpdateFilm() {
        //given
        Film film = Film.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .description("Крутой фильм")
                .duration(114)
                .build();
        Film filmNullDuration = film.toBuilder()
                .duration(null)
                .build();
        // when
        filmController.create(film);
        filmController.update(filmNullDuration);
        //then
        assertEquals(null, filmController.getFilmById(1L).getDuration());

    }
}
