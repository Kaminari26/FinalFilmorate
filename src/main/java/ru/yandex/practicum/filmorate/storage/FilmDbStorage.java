package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
@AllArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long create(Film film) {
        String filmSql = "INSERT INTO PUBLIC.FILM (NAME,DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID) " +
                "VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(filmSql, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (film.getGenres() != null) {
            addFilmGenre(id, film.getGenres());
        }
        return id;
    }

    private void addFilmGenre(long filmId, Set<Genre> genres) {
        String genresSql = "INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : genres) {
            Object[] args = {filmId, genre.getId()};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate(genresSql, batchArgs);
    }

    @Override
    public void update(Film film) throws FilmNotFoundException {
        String deleteGenreSql = "DELETE FROM " +
                "  FILM_GENRE " +
                "WHERE " +
                "  FILM_ID = ?";
        jdbcTemplate.update(deleteGenreSql, film.getId());

        String updateFilmSql = "UPDATE " +
                "  FILM " +
                "SET " +
                "  NAME = ?, " +
                "  DESCRIPTION = ?, " +
                "  RELEASE_DATE = ?, " +
                "  DURATION = ?, " +
                "  MPA_ID = ? " +
                "WHERE " +
                "  FILM_ID = ?";
        jdbcTemplate.update(
                updateFilmSql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        insertFilmGenres(film.getId(), film.getGenres());
    }

    private void insertFilmGenres(long filmId, Set<Genre> genres) {
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(
                        "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        filmId,
                        genre.getId()
                );
            }
        }
    }

    @Override
    public void delete(long id) {
        String sql = "" +
                "DELETE " +
                "FROM " +
                "  FILM " +
                "WHERE " +
                "  FILM_ID = ? ";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.MPA_ID, m.MPA_NAME, g.GENRE_ID, g.GENRE_NAME " +
                "FROM PUBLIC.FILM f " +
                "LEFT JOIN PUBLIC.MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN PUBLIC.FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN PUBLIC.GENRE g ON fg.GENRE_ID = g.GENRE_ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film findById(long id) throws FilmNotFoundException {
        String sql = "SELECT f.*, m.MPA_ID, m.MPA_NAME, g.GENRE_ID, g.GENRE_NAME " +
                "FROM PUBLIC.FILM f " +
                "LEFT JOIN PUBLIC.MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN PUBLIC.FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN PUBLIC.GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE f.FILM_ID = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (films.isEmpty()) {
            log.info("Фильм id = {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм id = %d не найден", id));
        }
        return films.get(0);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(
                rs.getLong("FILM_ID"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION")
        );

        Mpa mpa = new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME"));
        film.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        if (rs.getLong("GENRE_ID") != 0) {
            do {
                Genre genre = new Genre(rs.getLong("GENRE_ID"), rs.getString("GENRE_NAME"));
                genres.add(genre);
            } while (rs.next());
        }
        film.setGenres(genres);

        return film;
    }

}
