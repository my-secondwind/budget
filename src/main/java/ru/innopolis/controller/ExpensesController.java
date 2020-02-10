package ru.innopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.innopolis.domain.Operation;
import ru.innopolis.domain.User;
import ru.innopolis.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Расходы
 */
@Controller
public class ExpensesController {
    @Autowired
    OperationService operationService;

    /**
     * Список расходов пользователя
     */
    @GetMapping("/expenses/allexpensesuser")
    public String getAllExpensesUser(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Operation> operations = operationService.allExpensesUser(user.getUserid());
        model.addAttribute("allexpensesuser", operations);
        return "expenses";
    }

    /**
     * Добавить
     */
    @PostMapping("/expenses/add")
    public String saveExpenses(@ModelAttribute("addexpenses") Operation operation) {
        operationService.save(operation);
        return "expenses";
    }

    /**
     * Найти
     */
    @PostMapping("/expenses/find/{id}")
    public String findExpenses(Model model, @PathVariable("id") Long id) {
        Operation operation = operationService.findById(id);
        model.addAttribute("findexpenses", operation);
        return "expenses";
    }

    /**
     * Редактировать
     */
    @PostMapping("/expenses/update/{id}")
    public String updateExpenses(@ModelAttribute("updateexpenses") Operation operation) {
        operationService.save(operation);
        return "expenses";
    }

    /**
     * Удалить
     */
    @GetMapping("/expenses/delete/{id}")
    public String deleteExpenses(@PathVariable("id") Long id) {
        Operation operation = operationService.findById(id);
        operationService.delete(operation);
        return "expenses";
    }
}