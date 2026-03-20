package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
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

@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<DirectorDto> findAll() {
        return directorStorage.findAllDirectors()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(Long id) {
        Director director = directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден"));
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto addDirector(NewDirectorRequest request) {
        Director director = DirectorMapper.mapToDirector(request);
        director = directorStorage.addDirector(director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        Director director = directorStorage.getDirectorById(request.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + request.getId() + " не найден"));
        Director updatedDirector = DirectorMapper.updateDirectorFields(director, request);
        directorStorage.updateDirector(updatedDirector);
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public void deleteDirector(Long id) {
        if (directorStorage.getDirectorById(id).isEmpty()) {
            throw new NotFoundException("Режиссер с id " + id + " не найден");
        }
        directorStorage.deleteDirector(id);
    }
}
