package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Comparable<Film> {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    @Builder.Default
    Set<Long> likes = new HashSet<>();

    @Override
    public int compareTo(Film o) {
        return likes.size() - o.getLikes().size();
    }

}
