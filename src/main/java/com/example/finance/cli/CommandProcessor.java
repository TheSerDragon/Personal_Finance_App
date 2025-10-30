package com.example.finance.cli;

import com.example.finance.model.User;
import com.example.finance.service.UserService;
import com.example.finance.service.WalletService;
import com.example.finance.service.BudgetService;

import java.util.Scanner;

public class CommandProcessor {
    private final UserService userService = new UserService();
    private final WalletService walletService = new WalletService();
    private final BudgetService budgetService = new BudgetService();
    private User currentUser = null;
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Добро пожаловать в PersonalFinanceApp! Введите 'help' для списка команд.");
        while (true) {
            System.out.print(getPrompt());
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";
            try {
                switch (cmd) {
                    case "help": printHelp(); break;
                    case "register": cmdRegister(arg); break;
                    case "login": cmdLogin(arg); break;
                    case "logout": cmdLogout(); break;
                    case "add_income": cmdAddIncome(arg); break;
                    case "add_expense": cmdAddExpense(arg); break;
                    case "add_category": cmdAddCategory(arg); break;
                    case "set_budget": cmdSetBudget(arg); break;
                    case "edit_budget":
                        if (parts.length < 2) {
                            System.out.println("Использование: edit_budget <категория>:<новая_сумма>");
                            break;
                        }
                        String[] budgetParts = parts[1].split(":");
                        if (budgetParts.length < 2) {
                            System.out.println("Ошибка: формат — <категория>:<сумма>");
                            break;
                        }
                        String category = budgetParts[0].trim();
                        try {
                            double newAmount = Double.parseDouble(budgetParts[1].trim());
                            if (newAmount < 0) {
                                System.out.println("Ошибка: сумма не может быть отрицательной");
                                break;
                            }
                            currentUser.getWallet().setBudget(category, newAmount);
                            System.out.println("Бюджет категории '" + category + "' обновлён: " + String.format("%,.2f", newAmount));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка: сумма должна быть числом");
                        }
                        break;
                    case "summary": cmdSummary(arg); break;
                    case "list_ops": cmdListOps(arg); break;
                    case "totals": cmdTotals(arg); break;
                    case "export_json":
                        ensureLoggedIn();
                        walletService.exportToJson(currentUser);
                        break;
                    case "save": cmdSave(); break;
                    case "exit": cmdExit(); return;
                    default: System.out.println("Неизвестная команда. Введите 'help'."); break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private String getPrompt() {
        return currentUser == null ? "[guest] > " : "[" + currentUser.getName() + "] > ";
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  register <username> - регистрация");
        System.out.println("  login <username> - вход");
        System.out.println("  logout - выход");
        System.out.println("  add_category <category> - добавление категорий");
        System.out.println("  add_income <category>:<amount> - добавление доходов");
        System.out.println("  add_expense <category>:<amount> - добавление расходов");
        System.out.println("  set_budget <category>:<amount> - установка бюджета");
        System.out.println("  edit_budget <category>:<amount> - изменение бюджета");
        System.out.println("  summary - вывести сводку");
        System.out.println("  list_ops [n] - показать последние n операций");
        System.out.println("  totals [cat1,cat2,...] - суммы по выбранным категориям");
        System.out.println("  export_json - экспорт кошелька в JSON файл");
        System.out.println("  save - сохранить текущий кошелёк");
        System.out.println("  exit - сохранение и выход");
    }

    private void cmdRegister(String arg) {
        if (arg.isEmpty()) { System.out.println("Использование: register <username>"); return; }
        System.out.print("Введите пароль: ");
        String pw = scanner.nextLine();
        userService.register(arg.trim(), pw);
    }

    private void cmdLogin(String arg) {
        if (arg.isEmpty()) { System.out.println("Использование: login <username>"); return; }
        if (currentUser != null) { System.out.println("Сначала выполните logout."); return; }
        System.out.print("Введите пароль: ");
        String pw = scanner.nextLine();
        currentUser = userService.login(arg.trim(), pw);
        if (currentUser != null) {
            walletService.loadWallet(currentUser);
            System.out.println("Успешный вход. Баланс: " + String.format("%,.2f", currentUser.getWallet().getBalance()));

        }
    }

    private void cmdLogout() {
        if (currentUser == null) { System.out.println("Нет авторизованного пользователя."); return; }
        walletService.saveWallet(currentUser);
        System.out.println("Вы вышли. Данные сохранены.");
        currentUser = null;
    }

    private void ensureLoggedIn() {
        if (currentUser == null) throw new IllegalStateException("Сначала выполните вход: login <username>");
    }

    private void cmdAddIncome(String arg) {
        ensureLoggedIn();
        walletService.addIncome(currentUser, arg);
    }

    private void cmdAddExpense(String arg) {
        ensureLoggedIn();
        walletService.addExpense(currentUser, arg, scanner);
    }

    private void cmdAddCategory(String arg) {
        ensureLoggedIn();
        if (arg.isEmpty()) { System.out.println("Использование: add_category <category>"); return; }
        budgetService.addCategory(currentUser, arg.trim());
    }

    private void cmdSetBudget(String arg) {
        ensureLoggedIn();
        budgetService.setBudget(currentUser, arg);
    }

    private void cmdSummary(String arg) {
        ensureLoggedIn();
        walletService.printSummary(currentUser);
    }

    private void cmdListOps(String arg) {
        ensureLoggedIn();
        int n = 20;
        if (!arg.isEmpty()) {
            try { n = Integer.parseInt(arg.trim()); } catch (NumberFormatException ignored) {}
        }
        walletService.listOperations(currentUser, n);
    }

    private void cmdTotals(String arg) {
        ensureLoggedIn();
        walletService.totalsByCategories(currentUser, arg);
    }

    private void cmdSave() {
        if (currentUser == null) { System.out.println("Нет авторизованного пользователя — нечего сохранять."); return; }
        walletService.saveWallet(currentUser);
    }

    private void cmdExit() {
        if (currentUser != null) walletService.saveWallet(currentUser);
        userService.persistUsers();
        System.out.println("До свидания!");
    }
}