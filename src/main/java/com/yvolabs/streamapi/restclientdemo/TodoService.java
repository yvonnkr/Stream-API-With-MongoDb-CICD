package com.yvolabs.streamapi.restclientdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class TodoService {
    private final JsonPlaceholderClient jsonPlaceholderClient;

    public List<Todo> getTodos() {
        return Arrays.stream(jsonPlaceholderClient.getAllTodos())
                .map(this::mapResponseToTodo)
                .toList();

    }

    public Todo getTodoById(int id) {
        return mapResponseToTodo(jsonPlaceholderClient.getTodoById(id));
    }

    private Todo mapResponseToTodo(JsonPlaceHolderResponse response) {
        return Todo.builder()
                .id(response.getId())
                .title(response.getTitle())
                .completed(response.getCompleted())
                .build();
    }
}
