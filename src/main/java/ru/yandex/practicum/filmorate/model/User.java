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
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    @Builder.Default
    ArrayList<Long> friends = new ArrayList<>();
}
