package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    final private MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(long id) throws MpaNotFoundException {
        return mpaStorage.findById(id);
    }
}
