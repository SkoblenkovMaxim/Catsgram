package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts") // Указываем базовый путь
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "asc") String sort) {

        // Проверка на корректность параметра size
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }

        // Расчет значения from для пагинации
        int from = page * size;
        if (from < 0) {
            throw new ParameterNotValidException("from", "Параметр from не может быть меньше нуля");
        }

        // параметр sort должен содержать корректное значение

        // Вызов сервиса с заданными параметрами
        return postService.findAll(size, sort, from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/{postId}")
    public Post findPost(@PathVariable long postId) {
        return postService.findPostById(postId);
    }
}
