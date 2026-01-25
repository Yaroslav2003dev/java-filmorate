package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
