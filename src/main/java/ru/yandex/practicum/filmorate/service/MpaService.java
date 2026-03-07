package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    @Autowired
    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Collection<MpaDto> findAll() {
        List<MpaDto> mpaList = mpaRepository.findAll()
                .stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
        log.info("Получен список рейтингов MPA. Количество: {}", mpaList.size());
        return mpaList;
    }

    public MpaDto getMpaById(Long mpaId) {
        log.info("Поиск рейтинга MPA по id = {}", mpaId);
        return MpaMapper.mapToMpaDto(mpaRepository.findById(mpaId));
    }
}
