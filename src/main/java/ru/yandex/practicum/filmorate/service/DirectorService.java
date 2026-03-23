package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<DirectorDto> findAll() {
        log.debug("Запрос на получение всех режиссеров");
        return directorStorage.findAllDirectors()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(Long id) {
        log.debug("Запрос на получение режиссера с id: {}", id);
        Director director = directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден"));
        log.info("Найден режиссер: id={}, name={}", director.getId(), director.getName());
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto addDirector(NewDirectorRequest request) {
        log.debug("Запрос на добавление нового режиссера: {}", request);
        Director director = DirectorMapper.mapToDirector(request);
        director = directorStorage.addDirector(director);
        log.info("Добавлен новый режиссер: id={}, name={}", director.getId(), director.getName());
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        log.debug("Запрос на обновление режиссера с id: {}", request.getId());
        Director director = directorStorage.getDirectorById(request.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + request.getId() + " не найден"));
        Director updatedDirector = DirectorMapper.updateDirectorFields(director, request);
        directorStorage.updateDirector(updatedDirector);
        log.info("Обновлен режиссер: id={}, новое имя='{}'",
                updatedDirector.getId(), updatedDirector.getName());
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public void deleteDirector(Long id) {
        log.debug("Запрос на удаление режиссера с id: {}", id);
        if (directorStorage.getDirectorById(id).isEmpty()) {
            throw new NotFoundException("Режиссер с id " + id + " не найден");
        }
        directorStorage.deleteDirector(id);
        log.info("Удален режиссер с id: {}", id);
    }
}
