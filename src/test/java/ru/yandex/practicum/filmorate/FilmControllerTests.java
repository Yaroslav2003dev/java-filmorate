package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmControllerTests {
    @Autowired
    private FilmController filmController;

    @Test
    @DisplayName("Добавление фильма c правильными данными")
    void test_Create_WhenDataIsCorrect_CreateFilm() {
        //given
        NewFilmRequest film = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("Крутой фильм")
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        //when
        filmController.create(film);
        //then
        assertEquals(1, filmController.findAll().getBody().size());
    }

    @Test
    @DisplayName("Добавление фильма с пустым названием")
    public void test_Create_WhenEmptyName_NotCreateFilm() {
        //given
        NewFilmRequest filmNoName = NewFilmRequest.builder()
                .name(null)
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("Крутой фильм")
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmNoName)
        );
        assertEquals("название не может быть пустым", exception.getMessage());
    }

    @Test
    @DisplayName("Добавление фильма с большим описанием, в котором больше 200 символов")
    public void test_Create_WhenDescription201Characters_NotCreateFilm() {
        //given
        NewFilmRequest filmDescription201Characters = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description("a".repeat(201))
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmDescription201Characters)
        );
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    @DisplayName("Добавление фильма с нулевым описанием")
    public void test_Create_filmWithNullDescription_CreateFilm() {
        //given
        NewFilmRequest filmWithNullDescription = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(2011, Month.OCTOBER, 8))
                .description(null)
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when
        FilmDto filmDTO = filmController.create(filmWithNullDescription).getBody();
        //then
        assertTrue(filmController.getFilmById(filmDTO.getId()).getBody().getDescription() == null);
    }

    @Test
    @DisplayName("Добавление фильма с датой раньше чем 28 декабря 1895 года")
    public void test_Create_WhenOldReleaseBefore28December1895_NotCreateFilm() {
        //given
        NewFilmRequest filmOldRelease = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .description("Крутой фильм")
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmOldRelease)
        );
        assertEquals("дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
        assertTrue(filmController.findAll().getBody().isEmpty());
    }


    @Test
    @DisplayName("Добавление фильма c отрицательной продолжительностью")
    public void test_Create_WhenMinusDuration_NotCreateFilm() {
        //given
        NewFilmRequest filmMinusDuration = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .description("Крутой фильм")
                .duration(-123)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when & then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(filmMinusDuration)
        );
        assertEquals("продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление продолжительности фильма на null")
    public void test_Update_WhenNullDuration_NoUpdateFilm() {
        //given
        NewFilmRequest film = NewFilmRequest.builder()
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .description("Крутой фильм")
                .duration(114)
                .mpa(new Mpa(1L, "G"))
                .build();
        UpdateFilmRequest filmNullDuration = UpdateFilmRequest.builder()
                .id(1L)
                .name("Линкольн для адвоката")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .description("Крутой фильм")
                .duration(null)
                .mpa(new Mpa(1L, "G"))
                .build();
        // when
        filmController.create(film);
        filmController.update(filmNullDuration);
        //then
        assertEquals(0, filmController.getFilmByIdInner(1L).getDuration());
    }
}
