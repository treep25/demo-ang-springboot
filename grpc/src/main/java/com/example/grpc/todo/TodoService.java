package com.example.grpc.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoModel> getAllTodos() {
        return todoRepository.findAll(Pageable.ofSize(10).getSortOr(Sort.by(Sort.Direction.ASC, "whatToDo")));
    }

    public TodoModel createTodo(TodoModel todoModel) {
        todoModel.setStartDate();
        todoModel.setExpDate(60 * 60 * 24 * 4);
        return todoRepository.save(todoModel);
    }

    public TodoModel getById(long id) {
        return todoRepository.findById(id).orElseThrow();
    }

    public void deleteTodo(long id) {
        todoRepository.deleteById(id);
    }
}
