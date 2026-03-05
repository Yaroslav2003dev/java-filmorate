package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    @Autowired
    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Collection<MpaDto> findAll() {
        return mpaRepository.findAll()
                .stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMpaById(Long mpaId) {
        return MpaMapper.mapToMpaDto(mpaRepository.findById(mpaId));
    }
}
