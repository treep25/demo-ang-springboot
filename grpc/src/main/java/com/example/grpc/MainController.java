package com.example.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class MainController {

    @GetMapping("/todo-list")
    public String getTodosList() {
        return "todos-representation";
    }

}
