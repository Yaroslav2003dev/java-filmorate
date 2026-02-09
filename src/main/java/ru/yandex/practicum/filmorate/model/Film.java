package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;


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
    ArrayList<Long> likesIdUsers = new ArrayList<>();

    @Override
    public int compareTo(Film o) {
        return likesIdUsers.size() - o.getLikesIdUsers().size();
    }

}
