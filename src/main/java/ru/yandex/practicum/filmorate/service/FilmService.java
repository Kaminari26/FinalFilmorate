package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    @Autowired
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Autowired
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) throws FilmNotFoundException {
        long id = filmStorage.create(film);
        Film addedFilm = filmStorage.findById(id);
        log.info("Добавлен фильм: {}", addedFilm);
        return addedFilm;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long id) throws FilmNotFoundException {
        return filmStorage.findById(id);
    }

    public Film update(Film film) throws FilmNotFoundException {
        filmStorage.update(film);
        return film;
    }

    public void addLike(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        //film.getLikes().add(user.getId());
        filmStorage.update(film);
    }

    public void deleteLike(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
//        film.getLikes().remove(user.getId());
        filmStorage.update(film);
    }

    public List<Film> getTopLikedFilms(long count) {
        return new ArrayList<>();
    }

    public Film delete(long id) throws FilmNotFoundException {
        Film film = filmStorage.findById(id);
        filmStorage.delete(film);
        return film;
    }
}
