package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
@Data
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private Mpa mpa;
    private LocalDate releaseDate;
    private List<Genre> genres;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasDuration() {
        return !(duration == null || duration == 0);
    }

    public boolean hasMpa() {
        return !(mpa == null);
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasGenres() {
        return !(genres == null);
    }
}
