package com.yvolabs.streamapi.restclientdemo;

import com.yvolabs.streamapi.response.Result;
import com.yvolabs.streamapi.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Result<?> getAll() {
        List<Todo> todos = todoService.getTodos();

        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Get Todos Success: " + todos.size() + " todos found")
                .data(todos)
                .build();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable("id") int id) {
        Todo todo = todoService.getTodoById(id);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Get Todos By Id Success: ")
                .data(todo)
                .build();

    }
}
