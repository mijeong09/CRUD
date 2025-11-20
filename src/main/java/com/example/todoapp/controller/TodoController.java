package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoDto;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String todos(Model model) {
        // 이전에 만들었던 repository와 다른 객체를 사용하는거라 X
        // TodoRepository todoRepository = new TodoRepository();
//        List<TodoDto> todos = todoRepository.findAll();
        List<TodoDto> todos = todoService.getAllTodos();
        model.addAttribute("todos", todos);
        model.addAttribute("totalCount", todoService.getTotalCount());
        model.addAttribute("completedCount", todoService.getCompletedCount());
        model.addAttribute("activeCount", todoService.getActiveCount());
        return "todos";
    }

    @GetMapping("/new")
    public String newTodo(Model model) {
        model.addAttribute("todo", new TodoDto());
        return "form";
    }


//    @GetMapping("/create")
    @PostMapping
    public String create(
            @ModelAttribute TodoDto todo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            todoService.createTodo(todo);
            redirectAttributes.addFlashAttribute("message", "할 일이 생성되었습니다.");
            return "redirect:/todos";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/todos/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            TodoDto todo = todoService.getTodoById(id);
            model.addAttribute("todo", todo);
            return "detail";
        } catch (IllegalArgumentException e) {
            return "redirect:/todos";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Model model) {
//        todoRepository.deleteById(id);
        todoService.deleteTodoById(id);
        redirectAttributes.addFlashAttribute("message", "할 일이 삭제되었습니다");
        redirectAttributes.addFlashAttribute("status", "delete");
        return "redirect:/todos";
    }

    @GetMapping("/{id}/update")
    public String edit(@PathVariable Long id, Model model) {
        try {
//            TodoDto todo = todoRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("todo not found!"));
            TodoDto todo = todoService.getTodoById(id);
            model.addAttribute("todo", todo);
            return "form";
        } catch (IllegalArgumentException e){
            return "redirect:/todos";
        }
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
//            @RequestParam String title,
//            @RequestParam String content,
//            @RequestParam(defaultValue = "false") Boolean completed,
            @ModelAttribute TodoDto todo,
            RedirectAttributes redirectAttributes) {
        try {
//                TodoDto todo = todoRepository.findById(id)
//                        .orElseThrow();
//
//                todo.setTitle(title);
//                todo.setContent(content);
//                todo.setCompleted(completed);

//            todo.setId(id);
//            todoRepository.save(todo);

            todoService.updateTodoById(id, todo);
            redirectAttributes.addFlashAttribute("message", "할 일이 수정되었습니다.");

                return "redirect:/todos/" + id;
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("message", "없는 할 일입니다.");
            return "redirect:/todos";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
//        List<TodoDto> todos = todoRepository.findByTitleContaining(keyword);
        List<TodoDto> todos = todoService.searchTodos(keyword);
        model.addAttribute("todos", todos);

        return "todos";
    }

    @GetMapping("/active")
    public String active(Model model) {
//        List<TodoDto> todos = todoRepository.findByCompleted(false);
        List<TodoDto> todos = todoService.getTodosByCompleted(false);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/completed")
    public String completed(Model model) {
//        List<TodoDto> todos = todoRepository.findByCompleted(true);
        List<TodoDto> todos = todoService.getTodosByCompleted(true);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, Model model) {
        try {
//            TodoDto todo = todoRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("todo not found!"));
//            todo.setCompleted(!todo.isCompleted());
//            todoRepository.save(todo);
            todoService.toggleCompleted(id);
            return "redirect:/todos/" + id;
        } catch (IllegalArgumentException e){
            return "redirect:/todos";
        }
    }

    @GetMapping("/delete-completed")
    public String deleteCompleted(RedirectAttributes redirectAttributes) {
        todoService.deleteCompletedTodos();
        redirectAttributes.addFlashAttribute("message", "완료된 할일 삭제");
        return "redirect:/todos";
    }
}
