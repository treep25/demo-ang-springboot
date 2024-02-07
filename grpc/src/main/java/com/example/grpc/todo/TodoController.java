package com.example.grpc.todo;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/list")
    public ResponseEntity<?> getListOfTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @PostMapping("/new")
    public ResponseEntity<?> saveTodo(@RequestBody TodoModel todoModel) {
        TodoModel todo = todoService.createTodo(todoModel);
        return ResponseEntity.created(URI.create(String.format("/api/v1/todos/%s", todo.getId()))).body(todo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
