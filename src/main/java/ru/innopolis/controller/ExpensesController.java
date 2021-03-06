package ru.innopolis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.innopolis.domain.Account;
import ru.innopolis.domain.Category;
import ru.innopolis.domain.Operation;
import ru.innopolis.domain.User;
import ru.innopolis.service.AccountService;
import ru.innopolis.service.CategoryService;
import ru.innopolis.service.DateAnalizer;
import ru.innopolis.service.OperationService;
import ru.innopolis.service.dto.CategoryPeriod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static ru.innopolis.controller.IncomeController.MAX_COUNT_ELEMENT_PAGE;

/**
 * Расходы
 */
@Controller
@Slf4j
public class ExpensesController {
    private OperationService operationService;

    private AccountService accountService;

    private CategoryService categoryService;

    private DateAnalizer dateAnalizer;

    private int dateRange;

    private CategoryPeriod categoryPeriod;

    private int findIncome = 0;

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

    @Autowired
    public void setDateAnalizer(DateAnalizer dateAnalizer) {
        this.dateAnalizer = dateAnalizer;
    }

    /**
     * Список расходов пользователя
     */
    @GetMapping("/expenses")
    public String getAllExpensesUser(Model model, HttpServletRequest request, @ModelAttribute("period") String period, Integer page) {
        if (page == null) page = 1;
        Pageable pageable = PageRequest.of(page - 1, MAX_COUNT_ELEMENT_PAGE);
        User user = (User) request.getSession().getAttribute("user");
        List<Operation> operations = operationService.allExpensesUser(user.getFamem().getFamemid(), LocalDate.now(), LocalDate.now(), 0, pageable);
        model.addAttribute("countPage", page(user.getFamem().getFamemid(), LocalDate.now(), LocalDate.now(), 0));
        model.addAttribute("allExpensesUser", operations);
        HttpSession session = request.getSession(true);
        session.setAttribute("allcategoryperiod", null);
        List<Operation> operationsAll = operationService.allExpensesUser(user.getFamem().getFamemid(), LocalDate.now(), LocalDate.now(), 0, null);
        BigDecimal sumPeriod = BigDecimal.ZERO;
        for (Operation obj : operationsAll) {
            sumPeriod = sumPeriod.add(obj.getAmount());
        }
        model.addAttribute("intervalperiod", "СУММА ЗА ДЕНЬ ");
        model.addAttribute("sumperiod", sumPeriod + " руб.");
        if (findIncome == 0) {
            session.setAttribute("findallaccountbyusersortfilter",null);
        }
        if (findIncome == 1) {
            findIncome = 0;
        }
        session.setAttribute("isaccount", 1);
        session.setAttribute("curpage", page);
        return "expenses";
    }

    /**
     * Список расходов пользователя с разбивкой на страницы
     */
    @GetMapping(value = "/expenses{page}")
    public String listPartPage(Model model,
                               HttpServletRequest request,
                               @ModelAttribute("period") String period,
                               @PathVariable("page") Integer page) {
        getAllExpensesUser(model, request, period, page);
        return "expenses";
    }

    /**
     * Добавить
     */
    @PostMapping("/expenses/add")
    public String saveExpenses(HttpServletRequest request,
                               @ModelAttribute("expensesForm") Operation operation,
                               @ModelAttribute("accountid") Account account,
                               @ModelAttribute("categoryid") Category category) {
        if (operation.getOperationid() != null) {
            Operation operationOld = operationService.findById(operation.getOperationid());
            operationService.clearDelete(operationOld);
        }
        account = accountService.findById(account.getAccountid());
        category = categoryService.findById(category.getCategoryid());
        if (operation.getTypeoperationid().equals(2L)) {
            account.setAmount(account.getAmount().subtract(operation.getAmount()));
        }
        accountService.save(account);
        operation.setAccount(account);
        operation.setCategory(category);
        operationService.save(operation);
        HttpSession session = request.getSession(true);
        session.setAttribute("allcategoryperiod", null);
        return "redirect:/expenses";
    }

    /**
     * Найти
     */
    @PostMapping("/expenses/find/{id}")
    public String findExpenses(RedirectAttributes attributes, @PathVariable("id") Long id, HttpServletRequest request) {
        Operation operation = operationService.findById(id);
        attributes.addFlashAttribute("findexpenses", operation);
        User user = (User) request.getSession().getAttribute("user");
        List<Account> accountsByUser = accountService.findAllByUserSort(user.getUserid(), operation.getAccount().getAccountid());
        HttpSession session = request.getSession(true);
        session.setAttribute("findallaccountbyusersortfilter", accountsByUser.get(0).getName() + ": " + accountsByUser.get(0).getAmount() + " руб.");
        findIncome = 1;
        return "redirect:/expenses";
    }

    /**
     * Редактировать
     */
    @GetMapping("/expenses/{id}")
    public String renderUpdateExpenses(@PathVariable("id") Long id, RedirectAttributes attributes, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Operation> operations = operationService.allExpensesUser(user.getFamem().getFamemid(), LocalDate.now(), LocalDate.now(), 0, null);
        attributes.addFlashAttribute("allExpensesUser", operations);
        Operation updatedOperation = operationService.findById(id);
        attributes.addFlashAttribute("updatedOperation", updatedOperation);
        List<Account> accountsByUser = accountService.findAllByUserSort(user.getUserid(), updatedOperation.getAccount().getAccountid());
        HttpSession session = request.getSession(true);
        session.setAttribute("findallaccountbyusersortfilter", accountsByUser.get(0).getName() + ": " + accountsByUser.get(0).getAmount() + " руб.");
        findIncome = 1;
        return "redirect:/expenses";
    }

    /**
     * Удалить
     */
    @GetMapping("/expenses/delete/{id}")
    public String deleteExpenses(@PathVariable("id") Long id) {
        Operation operation = operationService.findById(id);
        if (operation != null) {
            operationService.clearDelete(operation);
        }
        return "redirect:/expenses";
    }

    @ModelAttribute
    public void setModel(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Account> accountsByUser = accountService.findAllByUser(user.getUserid());
        model.addAttribute("findallaccountbyuser", accountsByUser);
        model.addAttribute("refallcategory", categoryService.findAll());
    }

    /**
     * Фильтр
     */
    @PostMapping("/expenses/filter")
    public String filterIncome(Model model,
                               HttpServletRequest request,
                               @ModelAttribute("dateRange") int period,
                               @ModelAttribute("categoryperiod") CategoryPeriod category,
                               Integer page) {
        List<LocalDate> dates = dateAnalizer.parsePeriod(period);
        User user = (User) request.getSession().getAttribute("user");
        if (page == null) page = 1;
        Pageable pageable = PageRequest.of(page - 1, MAX_COUNT_ELEMENT_PAGE);
        List<Operation> operations = operationService.allExpensesUser(user.getFamem().getFamemid(), dates.get(0), dates.get(1), category.getCategoryperiod(), pageable);
        model.addAttribute("countPage", page(user.getFamem().getFamemid(), dates.get(0), dates.get(1), category.getCategoryperiod()));
        model.addAttribute("periodselected", period);
        model.addAttribute("allExpensesUser", operations);
        HttpSession session = request.getSession(true);
        session.setAttribute("allcategoryperiod", category.getCategoryperiod());
        if (period > 0 || category.getCategoryperiod() > 0) {
            model.addAttribute("isfilter", 1);
            dateRange = period;
            categoryPeriod = category;
        }

        List<Operation> operationsAll = operationService.allExpensesUser(user.getFamem().getFamemid(), dates.get(0), dates.get(1), category.getCategoryperiod(), null);
        BigDecimal sumPeriod = BigDecimal.ZERO;
        for (Operation operation : operationsAll) {
            sumPeriod = sumPeriod.add(operation.getAmount());
        }
        switch (period) {
            case 1: {
                model.addAttribute("intervalperiod", "СУММА ЗА ДЕНЬ ");
                model.addAttribute("sumperiod", sumPeriod + " руб.");
                break;
            }
            case 2: {
                model.addAttribute("intervalperiod", "СУММА ЗА НЕДЕЛЮ ");
                model.addAttribute("sumperiod", sumPeriod + " руб.");
                break;
            }
            case 3: {
                model.addAttribute("intervalperiod", "СУММА ЗА МЕСЯЦ ");
                model.addAttribute("sumperiod", sumPeriod + " руб.");
                break;
            }
            case 4: {
                model.addAttribute("intervalperiod", "СУММА ЗА ГОД ");
                model.addAttribute("sumperiod", sumPeriod + " руб.");
                break;
            }
            case 5: {
                model.addAttribute("intervalperiod", "СУММА ЗА ВЕСЬ ПЕРИОД ");
                model.addAttribute("sumperiod", sumPeriod + " руб.");
                break;
            }
        }
        session.setAttribute("curpage", page);
        return "expenses";
    }

    /**
     * Фильтр с учетом разбивки на страницы
     */
    @GetMapping(value = "/expenses/filter{page}")
    public String filterIncomePage(Model model,
                                   HttpServletRequest request,
                                   @PathVariable("page") Integer page) {
        filterIncome(model, request, dateRange, categoryPeriod, page);
        return "expenses";
    }

    private int page(Long userId, LocalDate dateStart, LocalDate end, int categoryid) {
        int allRecord = operationService.allExpensesUser(userId, dateStart, end, categoryid, null).size();
        return (allRecord % MAX_COUNT_ELEMENT_PAGE == 0) ? allRecord / MAX_COUNT_ELEMENT_PAGE : allRecord / MAX_COUNT_ELEMENT_PAGE + 1;
    }
}