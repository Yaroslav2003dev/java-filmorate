package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UpdateUserRequest {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }

    public boolean hasId() {
        return !(id == null);
    }
}

