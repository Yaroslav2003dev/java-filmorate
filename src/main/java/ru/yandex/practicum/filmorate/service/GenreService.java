package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<GenreDto> findAll() {
        List<GenreDto> genres = genreRepository.findAll()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
        log.info("Получен список жанров. Количество: {}", genres.size());
        return genres;
    }


    public GenreDto getGenreById(Long genreId) {
        log.info("Поиск жанра по id = {}", genreId);
        return GenreMapper.mapToGenreDto(genreRepository.findById(genreId));
    }

}
