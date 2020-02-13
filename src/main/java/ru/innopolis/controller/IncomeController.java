package ru.innopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.innopolis.domain.Account;
import ru.innopolis.domain.Category;
import ru.innopolis.domain.Operation;
import ru.innopolis.domain.User;
import ru.innopolis.service.AccountService;
import ru.innopolis.service.CategoryService;
import ru.innopolis.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * Доходы
 */
@Controller
public class IncomeController {
    private OperationService operationService;

    private AccountService accountService;

    private CategoryService categoryService;

    @Autowired
    public void setOperationService(OperationService operationService) {
        this.operationService = operationService;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Список доходов пользователя
     */
    @GetMapping("/income")
    public String getAllIncomeUser(Model model, HttpServletRequest request, @ModelAttribute("period") String period) {
        User user = (User) request.getSession().getAttribute("user");
        List<Object[]> operations = operationService.allIncomeUser(user.getUserid(), LocalDate.now(), LocalDate.now());
        model.addAttribute("allincomeuser", operations);
        return "income";
    }

    /**
     * Добавить
     */
    @PostMapping("/income/add")
    public String saveIncome(@ModelAttribute("addincome") Operation operation,
                             @ModelAttribute("accountid") Account account,
                             @ModelAttribute("categoryid") Category category) {
        account = accountService.findById(account.getAccountid());
        category = categoryService.findById(category.getCategoryid());
        if (operation.getTypeoperationid().equals(1L)) {
            account.setAmount(account.getAmount().add(operation.getAmount()));
        }
        accountService.save(account);
        operation.setAccount(account);
        operation.setCategory(category);
        operationService.save(operation);
        return "redirect:/income";
    }

    /**
     * Найти
     */
    @GetMapping("/income/find/{id}")
    public String findIncome(Model model, @PathVariable("id") Long id) {
        Operation operation = operationService.findById(id);
        model.addAttribute("findincome", operation);
        return "redirect:/income";
    }

    /**
     * Редактировать
     */
    @PostMapping("/income/update/{id}")
    public String updateIncome(@ModelAttribute("updincome") Operation operation) {
        operationService.save(operation);
        return "redirect:/income";
    }

    /**
     * Удалить
     */
    @GetMapping("/income/delete/{id}")
    public String deleteIncome(@PathVariable("id") Long id) {
        Operation operation = operationService.findById(id);
        operationService.delete(operation);
        return "redirect:/income";
    }
}