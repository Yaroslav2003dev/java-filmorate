package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private List<Genre> genres;
    private Mpa mpa;
    private List<Director> directors;
}
