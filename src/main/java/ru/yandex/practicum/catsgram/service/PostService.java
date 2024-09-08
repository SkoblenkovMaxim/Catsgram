package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int size, String sort, int from) {
        List<Post> sortedPosts = new ArrayList<>(posts.values());

        // Определяем порядок сортировки
        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            throw new ConditionsNotMetException("Неверный порядок сортировки: " + sort);
        }

        // Сортируем посты по дате создания в зависимости от порядка
        if (sortOrder == SortOrder.ASCENDING) {
            sortedPosts.sort(Comparator.comparing(Post::getPostDate));
        } else {
            sortedPosts.sort(Comparator.comparing(Post::getPostDate).reversed());
        }

        // Применяем пагинацию
        return sortedPosts.stream()
                .skip(from) // Пропускаем первые 'from' постов
                .limit(size) // Берем 'size' постов
                .collect(Collectors.toList()); // Собираем в список
    }

    public Post create(Post post) {
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id=" + post.getAuthorId() + " не неайден");
        }
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Post findPostById(Long postId) {
        return posts.values().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пост № %d не найден", postId)));
    }
}