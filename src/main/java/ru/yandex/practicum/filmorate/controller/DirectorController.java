package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Запрос всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Long id) {
        log.info("Запрос режиссера {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("Запрос создания режиссера: {}", director);
        return directorService.save(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("Запрос обновления режиссера: {}", director);
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable Long id) {
        log.info("Запрос удаления режиссера: {}", id);
        directorService.deleteById(id);
    }

}
