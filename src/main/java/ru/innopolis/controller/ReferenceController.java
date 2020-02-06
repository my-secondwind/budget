package ru.innopolis.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.innopolis.domain.*;
import ru.innopolis.service.*;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Справочники из бд
 */
@Controller
public class ReferenceController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private KinshipService kinshipService;

    private AccountService accountService;

    @Autowired
    private AccountTypeService accountTypeService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TypeOperationService typeOperationService;

    /**
     * Справочник ролей
     */
    @GetMapping("/ref/allrole")
    public List<Role> getAllRole(Model model, String page) {
        return roleService.findAll();
    }

    /**
     * Справочник родства
     */
    @GetMapping("/ref/allkinship")
    public List<Kinship> getAllKinship(Model model, String page) {
        return kinshipService.findAll();
    }


    /**
     * Справочник  счетов
     */
    @GetMapping("/ref/allaccounts")
    @ModelAttribute("refallaccounts")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    /**
     * Справочник типов счетов
     */
    @GetMapping("/ref/allaccounttype")
    @ModelAttribute("refallaccounttype")
    public List<AccountType> getAllAccountType() {
        return accountTypeService.findAll();
    }

    /**
     * Справочник валют
     */
    @GetMapping("/ref/allcurrency")
    public List<Currency> getAllCurrency(Model model, String page) {
        List<Currency> currencies = currencyService.findAll().stream()
                .filter(x -> x.getBrief().equals("rub"))
                .collect(Collectors.toList());
        return currencies;
    }

    /**
     * Справочник категорий
     */
    @GetMapping("/ref/allcategory")
    public List<Category> getAllCategory(Model model, String page) {
        return categoryService.findAll();
    }

    /**
     * Справочник типов операций
     */
    @GetMapping("/ref/alltypeoperation")
    public List<TypeOperation> getAllTypeOperation(Model model, String page) {
        return typeOperationService.findAll();
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}